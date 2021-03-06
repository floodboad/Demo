package com.jvirriel.demo.frontend.entitystatus.service;

import com.jvirriel.demo.model.entitystatus.EntityStatus;
import com.jvirriel.ui.service.AbstractService;
import com.jvirriel.ui.service.TService;
import com.jvirriel.demo.model.entitystatus.EntityStatus;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.jvirriel.demo.frontend.entitystatus.constants.EntityStatusServiceConstants.*;

/**
 * Servicio correspondiente a la funcionalidad entitystatus. Debe heredar de AbstractService, implementar a TService
 * y asociarle la entidad principal a implementar (de acuerdo al requerimiento) y el tipo de dato que tiene
 * el Id de dicha entidad.
 */
public class EntityStatusService extends AbstractService implements TService<EntityStatus, Integer> {

    @Override
    public void delete(Integer id) {
        try {
            restTemplate.exchange(requestStandard.deleteRequest(BY_ID + id), EntityStatus.class);
        } catch (Exception ignored) {
        }
    }

    @Override
    public Optional<List<EntityStatus>> findAll(String search, String orderBy, String page, String size) {
        List<EntityStatus> result = null;

        Map<String, String> header = buildHeader(search, orderBy, page, size);

        try {
            result = restTemplate.exchange(requestStandard.getRequest(ENTITY_STATUS_URI, header),
                    new ParameterizedTypeReference<List<EntityStatus>>() {
                    })
                    .getBody();
        } catch (Exception ignored) {
        }

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<EntityStatus> findById(Integer id) {
        EntityStatus entityStatus = null;
        try {
            entityStatus = restTemplate.exchange(requestStandard.getRequest(BY_ID.concat(id.toString())),
                    EntityStatus.class).getBody();
        } catch (Exception ignored) {
        }

        return Optional.ofNullable(entityStatus);
    }

    @Override
    public Optional<EntityStatus> save(EntityStatus entityStatus) {
        EntityStatus result = null;

        try {
            result = restTemplate.exchange(requestStandard.postRequest(SAVE, entityStatus), EntityStatus.class)
                    .getBody();
        } catch (Exception ignored) {
        }

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<EntityStatus> update(EntityStatus entityStatus) {
        EntityStatus result = null;

        try {
            result = restTemplate.exchange(requestStandard.putRequest(BY_ID + entityStatus.getId(), entityStatus),
                    EntityStatus.class).getBody();
        } catch (Exception ignored) {
        }

        return Optional.ofNullable(result);
    }
}