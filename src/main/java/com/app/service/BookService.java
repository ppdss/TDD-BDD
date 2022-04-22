package com.app.service;

import java.util.Optional;

import com.app.model.entity.Book;

public interface BookService {

	Book save(Book book);
	Optional<Book> getById(Long id);
	
}
