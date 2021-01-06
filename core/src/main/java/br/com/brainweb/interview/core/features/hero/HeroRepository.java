package br.com.brainweb.interview.core.features.hero;

import br.com.brainweb.interview.model.Hero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HeroRepository extends JpaRepository<Hero, UUID> {

    Optional<Hero> findByNameAndBreed(String name, String breed);

    Optional<Hero> findByName(String name);
}
