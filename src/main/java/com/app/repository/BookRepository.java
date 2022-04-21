package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.model.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long>{

	boolean existsByIsbn(String isbn);

}
