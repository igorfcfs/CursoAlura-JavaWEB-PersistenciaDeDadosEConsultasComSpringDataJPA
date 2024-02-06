package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Serie;

import org.springframework.data.jpa.repository.JpaRepository;

//CRUD - CREATE, READ, UPDADE, DELETE operations in database

public interface SerieRepository extends JpaRepository<Serie, Long> {
	
}
