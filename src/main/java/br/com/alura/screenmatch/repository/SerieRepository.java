package br.com.alura.screenmatch.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Serie;

//CRUD - CREATE, READ, UPDADE, DELETE operations in database

public interface SerieRepository extends JpaRepository<Serie, Long> {
	Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);
	
	List<Serie> findByAtoresContainingIgnoreCase(String nomeAtor);
	List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, Double avaliacao);
	
	List<Serie> findTop5ByOrderByAvaliacao();
	List<Serie> findTop5ByOrderByAvaliacaoDesc();
	
	List<Serie> findByGenero(Categoria categoria);
	
	List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(Integer temporadas, Double avaliacao);
	
}
