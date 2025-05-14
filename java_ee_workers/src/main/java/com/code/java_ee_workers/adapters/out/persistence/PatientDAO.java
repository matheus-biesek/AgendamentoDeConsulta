package com.code.java_ee_workers.adapters.out.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.code.java_ee_workers.domain.dto.request.UpdatePatientDTO;
import com.code.java_ee_workers.domain.model.Patient;
import com.code.lightquery.LightQuery;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PatientDAO extends LightQuery<Patient> {

    public PatientDAO() {
        super(Patient.class, "profile_service");
    }

    @SuppressWarnings("unchecked")
    public Optional<Patient> findByUserId(UUID userId) {
        try {
            List<Object[]> results = entityManager.createNativeQuery(
                "SELECT patient_id, user_id, allergies, blood_type, weight, height, active FROM profile_service.patient WHERE user_id = ?")
                    .setParameter(1, userId)
                    .getResultList();

            if (results.isEmpty()) {
                return Optional.empty();
            }

            Object[] result = results.get(0);

            return Optional.of(new Patient(
                (UUID) result[0],
                (UUID) result[1],
                (String) result[2],
                (String) result[3],
                (Double) result[4],
                (Double) result[5],
                (Boolean) result[6]
            ));
        } catch (Exception e) { 
            throw new RuntimeException("Erro inesperado ao buscar paciente por userId!", e);
        }
    }

    @Transactional
    public void updateByUserId(UpdatePatientDTO updatePatientDTO) {
        if (updatePatientDTO.getUserId() == null) {
            throw new RuntimeException("ID do paciente é obrigatório para atualização");
        }

        StringBuilder queryBuilder = new StringBuilder("UPDATE profile_service.patient SET ");
        Map<String, Object> params = new HashMap<>();

        if (updatePatientDTO.getId() != null) {
            queryBuilder.append("patient_id = :patientId, ");
            params.put("patientId", updatePatientDTO.getId());
        }

        if (updatePatientDTO.getActive() != null) {
            queryBuilder.append("active = :active, ");
            params.put("active", updatePatientDTO.getActive());
        }

        if (updatePatientDTO.getAllergies() != null) {
            queryBuilder.append("allergies = :allergies, ");
            params.put("allergies", updatePatientDTO.getAllergies());
        }

        if (updatePatientDTO.getBloodType() != null) {
            queryBuilder.append("blood_type = :bloodType, ");
            params.put("bloodType", updatePatientDTO.getBloodType());
        }

        if (updatePatientDTO.getWeight() != null) {
            queryBuilder.append("weight = :weight, ");
            params.put("weight", updatePatientDTO.getWeight());
        }

        if (updatePatientDTO.getHeight() != null) {
            queryBuilder.append("height = :height, ");
            params.put("height", updatePatientDTO.getHeight());
        }
        
        queryBuilder.setLength(queryBuilder.length() - 2);
        queryBuilder.append(" WHERE user_id = :userId");
        params.put("userId", updatePatientDTO.getUserId());

        try {
            Query query = entityManager.createNativeQuery(queryBuilder.toString());

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            int updatedRows = query.executeUpdate();

            if (updatedRows == 0) {
                throw new RuntimeException("Paciente não encontrado para atualização!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao atualizar paciente!", e);
        }
    }
}
