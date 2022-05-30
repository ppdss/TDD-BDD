package api.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import api.model.entity.Book;

public interface BookService {

	Book save(Book book);
	Optional<Book> getById(Long id);
	void delete(Book book);
	Book update(Book book);
	Page<Book> find(Book book, Pageable page);
	Optional<Book> getBookByIsbn(String string);
	
}
