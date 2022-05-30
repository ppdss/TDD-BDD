package api.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import api.exception.BusinessException;
import api.model.entity.Book;
import api.model.repository.BookRepository;
import api.service.BookService;

@Service
public class BookServiceImpl implements BookService {

	private BookRepository repository;
	
	public BookServiceImpl(BookRepository repository) {
		this.repository=repository;
	}
	
	/**
	 * Insere livro
	 * @param  Book book
	 * @return Book
	 */
	public Book save(Book book) {	
		if(this.repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn já cadastrado.");
		}
		return this.repository.save(book);
	}

	/**
	 * Busca livro por id
	 * @param  Long id
	 * @return Optional<Book>
	 */
	public Optional<Book> getById(Long id) {
		return this.repository.findById(id);
	}


	/**
	 * Deleta livro
	 * @param Book book
	 * @throws IllegalArgumentException("Book id cant be null")
	 */
	public void delete(Book book) {
		if(book == null || book.getId() == null) {
			throw new IllegalArgumentException("Book id cant be null");
		}
		this.repository.delete(book);
	}

	/**
	 * Atualiza livro
	 * @param Book book
	 * @throws IllegalArgumentException("Book id cant be null")
	 * @return Book
	 */
	@Override
	public Book update(Book book) {
		if(book == null || book.getId() == null) {
			throw new IllegalArgumentException("Book id cant be null");
		}
		return this.repository.save(book);
	}


	/**
	 * Busca paginada de livro com filtro. 
	 * (Ex livros que tenham a palavra Arthur)
	 * @param Book book
	 * @param Pageable pageRequest
	 * @return Page<Book>
	 */
	public Page<Book> find(Book book, Pageable pageRequest) {
		Example<Book> example  = Example.of(book,
				ExampleMatcher
					.matching()
					.withIgnoreCase()
					.withIgnoreNullValues()
					.withStringMatcher( ExampleMatcher.StringMatcher.CONTAINING));
		
		return repository.findAll(example, pageRequest);
	}


	public Optional<Book> getBookByIsbn(String string) {
		return null;
	}

}
