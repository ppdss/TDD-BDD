package com.app.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.app.exception.BusinessException;
import com.app.model.entity.Book;
import com.app.repository.BookRepository;
import com.app.service.BookService;

@Service
public class BookServiceImpl implements BookService {

	private BookRepository repository;
	
	public BookServiceImpl(BookRepository repository) {
		this.repository=repository;
	}
	
	
	public Book save(Book book) {	
		if(repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn já cadastrado.");
		}
		return repository.save(book);
	}

	public Optional<Book> getById(Long id) {
		return Optional.empty();
	}



	public void delete(Book book) {
		
	}

}
