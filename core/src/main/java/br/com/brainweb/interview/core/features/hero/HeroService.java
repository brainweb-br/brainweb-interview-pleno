package br.com.brainweb.interview.core.features.hero;

import br.com.brainweb.interview.core.features.hero.exception.*;
import br.com.brainweb.interview.model.DtoHeroResponse;
import br.com.brainweb.interview.model.Hero;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class HeroService {
    private final HeroRepository heroRepository;
    private static final Logger logger = LoggerFactory.getLogger(HeroService.class);

    public DtoHeroResponse createHero(Hero newHero) {
        logger.info("Hero received: {}", newHero.toString());
        Optional<Hero> dbHero = heroRepository.findByNameAndBreed(newHero.getName(), newHero.getBreed());

        if (dbHero.isPresent()) {
            throw new ConflictError("Hero already exists");
        }

        try {
            newHero.getPowerStats().setCreationDate(formatDate());
            newHero.getPowerStats().setUpdateDate(formatDate());
            newHero.setCreationDate(formatDate());
            newHero.setUpdateDate(formatDate());
            logger.info("Saving hero into database");
            return new DtoHeroResponse(heroRepository.save(newHero));
        } catch (Exception e) {
            throw new GenericErrors(e.getMessage());
        }
    }

    public DtoHeroResponse findHeroById(String id) {
        logger.info("Hero id: {}", id);

        validateId(id);
        Optional<Hero> hero = heroRepository.findById(UUID.fromString(id));
        if (hero.isEmpty()) {
            throw new NotFoundEntity("Herói nao encontrado");
        }
        return new DtoHeroResponse(hero.get());
    }

    public List<DtoHeroResponse> filterHeroesByName(List<String> heroesNames) {
        logger.info("Hero id: {}", heroesNames);

        List<DtoHeroResponse> heroesList = new ArrayList<>();
        heroesNames.forEach(name -> {
            Optional<Hero> hero = heroRepository.findByName(name);

            if (hero.isEmpty()) {
                throw new NotFoundEntity("Herói " + name + " nao encontrado");
            }
            heroesList.add(new DtoHeroResponse(hero.get()));
        });
        return heroesList;
    }

    public DtoHeroResponse updateHero(Hero newHero, String id) {
        logger.info("New hero information received: {}", newHero);
        logger.info("Updating Hero with id: {}", id);
        AtomicReference<Hero> dto = new AtomicReference<>();

        validateId(id);
        Optional<Hero> hero = heroRepository.findById(UUID.fromString(id));
        if (hero.isEmpty()) {
            throw new NotFoundEntity("Herói nao encontrado");
        }

        hero.ifPresent(heroToBeUpdated -> {
            heroToBeUpdated.setName(newHero.getName());
            heroToBeUpdated.setBreed(newHero.getBreed());
            heroToBeUpdated.getPowerStats().setStrength(newHero.getPowerStats().getStrength());
            heroToBeUpdated.getPowerStats().setAgility(newHero.getPowerStats().getAgility());
            heroToBeUpdated.getPowerStats().setDexterity(newHero.getPowerStats().getDexterity());
            heroToBeUpdated.getPowerStats().setIntelligence(newHero.getPowerStats().getIntelligence());
            heroToBeUpdated.getPowerStats().setUpdateDate(formatDate());
            heroToBeUpdated.setIsEnabled(newHero.getIsEnabled());
            heroToBeUpdated.setUpdateDate(formatDate());
            heroRepository.save(heroToBeUpdated);
            dto.getAndSet(heroToBeUpdated);
        });

        return new DtoHeroResponse(dto.get());
    }

    private OffsetDateTime formatDate() {
        return OffsetDateTime.now(ZoneId.of("America/Sao_Paulo"));
    }

    private void validateId(String id) {
        if ((id == null || (" ").equalsIgnoreCase(id) || ("").equalsIgnoreCase(id))) {
            throw new NoContentError("Id não pode ser nulo, vazio ou apenas um espaço em branco");
        }
    }
}
