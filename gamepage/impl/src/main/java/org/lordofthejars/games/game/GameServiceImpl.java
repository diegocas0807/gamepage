package org.lordofthejars.games.game;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import org.lordofthejars.games.game.api.Game;
import org.lordofthejars.games.game.api.GameService;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
@Transactional
class GameServiceImpl implements GameService {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Optional<Game> findGameById(long gameId) {

        final CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("database");

        CheckedFunction0<Optional<Game>> checkedSupplier = CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () ->
            Optional.ofNullable(entityManager.find(Game.class, gameId))
        );

        // TODO log exception
        Try<Optional<Game>> result = Try.of(checkedSupplier)
            .recover(throwable -> Optional.empty());

        return result.get();

    }
}
