package br.com.brainweb.interview.core.features.powerstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.brainweb.interview.model.PowerStats;

@Repository
public interface PowerStatsRepository extends JpaRepository<PowerStats, Integer> {

	PowerStats findById(int id);
	

}
