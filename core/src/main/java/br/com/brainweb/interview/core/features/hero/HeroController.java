package br.com.brainweb.interview.core.features.hero;

import br.com.brainweb.interview.core.features.exception.NotFoundException;
import br.com.brainweb.interview.core.features.powerstats.PowerStatsRepository;
import br.com.brainweb.interview.dto.CompareHeroDTO;
import br.com.brainweb.interview.dto.HeroDTO;
import br.com.brainweb.interview.dto.PowerStatsDTO;
import br.com.brainweb.interview.model.Hero;
import br.com.brainweb.interview.model.PowerStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/heros")
public class HeroController {

	@Autowired
	private HeroService heroService;

	@Autowired
	private HeroRepository heroRepository;

	@Autowired
	private PowerStatsRepository powerStatsRepository;

	@GetMapping("/find")
	public List<Hero> findByName(@RequestParam("name") String name) {
		List<Hero> hero = heroRepository.findAllByName(name);
		return hero;
	}

	@GetMapping("/{id}/find")
	public Hero find(@PathVariable("id") UUID id) {
		Hero hero = heroRepository.findById(id).orElseThrow(NotFoundException::new);
		return hero;
	}

	@GetMapping("/{id}/compare/{id2}")
	public CompareHeroDTO compare(@PathVariable("id1") UUID id1,@PathVariable("id2")  UUID id2) {
		Hero hero = heroRepository.findById(id1).orElseThrow(NotFoundException::new);
		Hero hero2 = heroRepository.findById(id2).orElseThrow(NotFoundException::new);

		return heroService.compareHeros(hero,hero2);
	}

	@PatchMapping("/{id}/delete")
	public void delete(@PathVariable("id") UUID id) {
		Hero hero = heroRepository.findById(id).orElseThrow(NotFoundException::new);
		heroRepository.delete(hero);
		powerStatsRepository.delete(hero.getPowerStats());

	}

	@PatchMapping("{id}/update")
	public Hero update(@PathVariable("id") UUID id, @RequestBody HeroDTO heroUpdate) {
		Hero hero = heroRepository.findById(id).orElseThrow(NotFoundException::new);
		return heroService.update(hero,heroUpdate);
	}

	@PatchMapping("/create")
	public Hero create(@RequestBody HeroDTO heroDTO) {

		return heroService.create(heroDTO);

	}


}
