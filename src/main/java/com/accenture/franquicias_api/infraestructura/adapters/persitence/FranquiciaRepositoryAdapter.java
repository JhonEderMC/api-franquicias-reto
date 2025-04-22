package com.accenture.franquicias_api.infraestructura.adapters.persitence;

import com.accenture.franquicias_api.infraestructura.adapters.persitence.data.Franquicia;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Repository
public class FranquiciaRepositoryAdapter {

    private static final Logger logger = LoggerFactory.getLogger(FranquiciaRepositoryAdapter.class);
    private final ReactiveMongoTemplate mongoTemplate;


    public Mono<Franquicia> saveFranquicia(Franquicia franquicia) {
        return mongoTemplate.save(franquicia)
                .onErrorResume(error -> {
                    logger.error("Error al guardar franquicia: {}", error.getMessage());
                    return Mono.error(error);
                });
    }

    public Mono<Franquicia> findFranquiciaByName(String nombre) {
        Criteria criteria = Criteria.where("nombre").is(nombre);
        Query query = new Query().addCriteria(criteria);
        return mongoTemplate.findOne(query, Franquicia.class)
                .onErrorResume(error -> {
                    logger.error("Error al obtener franquicia: {}", error.getMessage());
                    return Mono.error(error);
                });
    }

    public Mono<Franquicia> updateFranquicia(String id, Franquicia franquicia) {
        Query query = Query.query(Criteria.where("id").is(id));

        Update update = new Update()
                .set("nombre", franquicia.getNombre())
                .set("sucursales", franquicia.getSucursales());

        return mongoTemplate.updateFirst(query, update, Franquicia.class)
                .flatMap(result -> {
                    if (result.getModifiedCount() > 0) {
                        return mongoTemplate.findById(id, Franquicia.class);
                    } else {
                        return Mono.empty();
                    }
                })
                .onErrorResume(error -> {
                    logger.error("Error al actualizar franquicia: {}", error.getMessage());
                    return Mono.error(error);
                });
    }

}
