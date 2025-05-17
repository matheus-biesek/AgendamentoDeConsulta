package com.code.java_ee_workers.adapters.in.service;

import com.code.java_ee_workers.adapters.out.producer.SearchUserProducer;
import com.code.java_ee_workers.adapters.out.persistence.PatientDAO;
import com.code.java_ee_workers.domain.model.Patient;
import com.code.java_ee_workers.domain.dto.request.UpdatePatientDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

@ApplicationScoped
public class PatientService {

    @Inject
    private SearchUserProducer searchUserProducer;
    @Inject
    private PatientDAO patientDAO;

    public UUID createPatient(String uuidString, String allergies, String bloodType, Double height) {
        String response = searchUserProducer.sendAndReceive(uuidString);
        
        // Converte a string de resposta para um Map
        Map<String, Object> userData = parseResponseToMap(response);
        
        // Verifica se o usuário existe
        if (userData == null || !userData.containsKey("id")) {
            throw new RuntimeException("Usuário não encontrado");
        }
        
        // Extrai o UUID do usuário da resposta
        UUID userId = UUID.fromString(userData.get("id").toString());
        
        // Cria um novo UUID para o paciente
        UUID patientId = UUID.randomUUID();
        
        // Cria o objeto Patient com os dados necessários
        Patient patient = new Patient(
            patientId,
            userId,
            allergies,
            bloodType,
            height,
            true  // active - inicia como true
        );
        
        patientDAO.create(patient);
        
        return patientId;
    }
    
    public UpdatePatientDTO search(String uuId) {
        try {
            Patient patient = patientDAO.findById(UUID.fromString(uuId));
            if (patient == null) {
                throw new RuntimeException("Paciente não encontrado");
            }
            return new UpdatePatientDTO(
                patient.getPatient_id(),
                patient.getUser_id(),
                patient.getAllergies(),
                patient.getBlood_type(),
                patient.getHeight(),
                patient.getActive()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar paciente: " + e.getMessage(), e);
        }
    }

    public UpdatePatientDTO searchByUserId(String uuId) {
        try {
            Optional<Patient> patient = patientDAO.findByUserId(UUID.fromString(uuId));
            if (patient == null) {
                throw new RuntimeException("Paciente não encontrado");
            }
            return new UpdatePatientDTO(
                patient.get().getPatient_id(),
                patient.get().getUser_id(),
                patient.get().getAllergies(),
                patient.get().getBlood_type(),
                patient.get().getHeight(),
                patient.get().getActive()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao buscar paciente", e);
        }
    }

    public List<Patient> searchByActive(boolean active) {
        return patientDAO.findPatientByActive(active);
    }

    public void updatePatient(UpdatePatientDTO updatePatientDTO) {
        patientDAO.updateByUserId(updatePatientDTO);
    }

    private Map<String, Object> parseResponseToMap(String response) {
        try {
            Map<String, Object> userMap = new HashMap<>();
            String[] lines = response.split(",");
            
            for (String line : lines) {
                line = line.trim();
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    
                    if (key.equals("roles")) {
                        // Trata o array de roles
                        value = value.replace("[", "").replace("]", "").trim();
                        String[] roles = value.split(",");
                        List<String> rolesList = new ArrayList<>();
                        for (String role : roles) {
                            rolesList.add(role.trim());
                        }
                        userMap.put(key, rolesList);
                    } else {
                        // Trata valores simples
                        userMap.put(key, value);
                    }
                }
            }
            
            return userMap;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar resposta: " + e.getMessage(), e);
        }
    }
}
