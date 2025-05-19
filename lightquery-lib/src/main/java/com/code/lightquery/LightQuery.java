package com.code.lightquery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import jakarta.persistence.PersistenceContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class LightQuery<T> {
    
    @PersistenceContext
    protected EntityManager entityManager;
    protected final Class<T> entityClass;
    protected final String tableName;
    protected final String schemaName;
    
    protected LightQuery(Class<T> entityClass, String schemaName) {
        this.entityClass = entityClass;
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        this.tableName = tableAnnotation != null ? tableAnnotation.name() : entityClass.getSimpleName().toLowerCase();
        this.schemaName = schemaName;
    }

    @Transactional
    public void create(T entity) {
        StringBuilder queryBuilder = new StringBuilder("INSERT INTO " + schemaName + "." + tableName + " (");
        StringBuilder valuesBuilder = new StringBuilder(") VALUES (");
        List<Object> params = new ArrayList<>();
        int paramCount = 1;

        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(entity);
                if (value != null) {
                    if (paramCount > 1) {
                        queryBuilder.append(", ");
                        valuesBuilder.append(", ");
                    }
                    String columnName = convertToSnakeCase(field.getName());
                    queryBuilder.append(columnName);
                    
                    // Tratamento especial para tipos de dados
                    if (value instanceof java.sql.Time) {
                        valuesBuilder.append("?::time");
                    } else if (value instanceof java.util.UUID) {
                        valuesBuilder.append("?::uuid");
                    } else if (value instanceof Integer) {
                        valuesBuilder.append("?::integer");
                    } else {
                        valuesBuilder.append("?");
                    }
                    
                    params.add(value);
                    paramCount++;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Erro ao acessar campo da entidade", e);
            }
        }

        queryBuilder.append(valuesBuilder).append(")");

        try {
            Query query = entityManager.createNativeQuery(queryBuilder.toString());
            
            for (int i = 0; i < params.size(); i++) {
                query.setParameter(i + 1, params.get(i));
            }

            query.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao criar entidade!", e);
        }
    }

    @Transactional
    public void update(T entity) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE " + schemaName + "." + tableName + " SET ");
        Map<String, Object> params = new HashMap<>();
        UUID id = null;

        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(entity);
                if (value != null) {
                    String fieldName = field.getName();
                    if (fieldName.equals(tableName + "_id")) {
                        id = (UUID) value;
                        continue;
                    }
                    
                    if (!params.isEmpty()) {
                        queryBuilder.append(", ");
                    }
                    String columnName = convertToSnakeCase(fieldName);
                    queryBuilder.append(columnName).append(" = :").append(fieldName);
                    params.put(fieldName, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Erro ao acessar campo da entidade", e);
            }
        }

        if (id == null) {
            throw new RuntimeException("ID da entidade é obrigatório para atualização");
        }

        queryBuilder.append(" WHERE ").append(tableName).append("_id = :id");
        params.put("id", id);

        try {
            Query query = entityManager.createNativeQuery(queryBuilder.toString());
            
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            query.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao atualizar entidade!", e);
        }
    }

    @Transactional
    public void delete(UUID id) {
        if (id == null) {
            throw new RuntimeException("ID da entidade é obrigatório para deleção");
        }

        String query = "DELETE FROM " + schemaName + "." + tableName + 
                      " WHERE " + tableName + "_id = :id";

        try {
            Query nativeQuery = entityManager.createNativeQuery(query);
            nativeQuery.setParameter("id", id);
            nativeQuery.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao deletar entidade!", e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        String query = "SELECT * FROM " + schemaName + "." + tableName;
        return entityManager.createNativeQuery(query, entityClass).getResultList();
    }

    @SuppressWarnings("unchecked")
    public T findById(UUID id) {
        try {
            String query = "SELECT * FROM " + schemaName + "." + tableName + 
                      " WHERE " + tableName + "_id = ?";
            
            List<Object[]> results = entityManager.createNativeQuery(query)
                              .setParameter(1, id)
                              .getResultList();

            if (results.isEmpty()) {
                return null;
            }

            Object[] result = results.get(0);
            T entity = entityClass.getDeclaredConstructor().newInstance();
            
            int i = 0;
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = result[i];
                
                // Tratamento especial para tipos de dados
                if (value instanceof String && field.getType() == UUID.class) {
                    value = UUID.fromString((String) value);
                } else if (value instanceof java.math.BigDecimal && field.getType() == Double.class) {
                    value = ((java.math.BigDecimal) value).doubleValue();
                }
                
                field.set(entity, value);
                i++;
            }
            
            return entity;
        } catch (Exception e) {
            String errorMessage = String.format("Erro ao buscar entidade com ID %s. Detalhes: %s", 
                id, e.getMessage());
            throw new RuntimeException(errorMessage, e);
        }
    }

    private String convertToSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
