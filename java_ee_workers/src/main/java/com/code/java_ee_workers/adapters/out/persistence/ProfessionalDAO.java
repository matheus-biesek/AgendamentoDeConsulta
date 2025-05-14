package com.code.java_ee_workers.adapters.out.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.code.java_ee_workers.domain.dto.request.UpdateProfessionaDTO;
import com.code.java_ee_workers.domain.model.Professional;
import com.code.lightquery.LightQuery;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ProfessionalDAO extends LightQuery<Professional> {

    protected ProfessionalDAO() {
        super(Professional.class, "profile_service");
    }

    @SuppressWarnings("unchecked")
    public Optional<Professional> findByUserId(UUID userId) {
        try {
            List<Object[]> results = entityManager.createNativeQuery(
                "SELECT professional_id, user_id, active, professional_type, registry_number, specialization FROM profile_service.professional WHERE user_id = ?")
                    .setParameter(1, userId)
                    .getResultList();
                    
            if (results.isEmpty()) {
                return Optional.empty();
            }

            Object[] result = results.get(0);
            return Optional.of(new Professional(
                (UUID) result[0],
                (UUID) result[1],
                (Boolean) result[2],
                (String) result[3],
                (String) result[4],
                (String) result[5]
            ));
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao buscar profissional por userId!", e);
        }
    }

    @Transactional
    public void updateByUserId(UpdateProfessionaDTO updateProfessionaDTO) {
        if (updateProfessionaDTO.getUserId() == null) {
            throw new RuntimeException("ID do usuário é obrigatório para atualização");
        }

        StringBuilder queryBuilder = new StringBuilder("UPDATE profile_service.professional SET ");
        Map<String, Object> params = new HashMap<>();

        if (updateProfessionaDTO.getId() != null) {
            queryBuilder.append("professional_id = :professionalId, ");
            params.put("professionalId", updateProfessionaDTO.getId());
        }

        if (updateProfessionaDTO.getProfessionalType() != null) {
            queryBuilder.append("professional_type = :professionalType, ");
            params.put("professionalType", updateProfessionaDTO.getProfessionalType().name());
        }

        if (updateProfessionaDTO.getRegistryNumber() != null) {
            queryBuilder.append("registry_number = :registryNumber, ");
            params.put("registryNumber", updateProfessionaDTO.getRegistryNumber());
        }

        if (updateProfessionaDTO.getSpecialization() != null) {
            queryBuilder.append("specialization = :specialization, ");
            params.put("specialization", updateProfessionaDTO.getSpecialization().name());
        }

        if (updateProfessionaDTO.getActive() != null) {
            queryBuilder.append("active = :active, ");
            params.put("active", updateProfessionaDTO.getActive());
        }
        
        queryBuilder.setLength(queryBuilder.length() - 2);
        queryBuilder.append(" WHERE user_id = :userId");
        params.put("userId", updateProfessionaDTO.getUserId());

        try {
            Query query = entityManager.createNativeQuery(queryBuilder.toString());

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

                int updatedRows = query.executeUpdate();

                if (updatedRows == 0) {
                    throw new RuntimeException("Profissional não encontrado para atualização!");
                }
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao atualizar profissional!", e);
        }
    }
}
