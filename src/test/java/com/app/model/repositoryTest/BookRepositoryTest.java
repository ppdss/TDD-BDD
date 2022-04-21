package com.app.model.repositoryTest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.app.model.entity.Book;
import com.app.repository.BookRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest // Cleans database before and after execute the tests 
public class BookRepositoryTest {
	
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	BookRepository repository;
	
	@Test
	@DisplayName("Deve retornar verdadeiro quando existir o livro na base com o isbn informado.")
	public void shouldReturnTrueWhenIsbnExists() {
		
		// cenario
		String isbn = "123";
		Book book = Book.builder().isbn(isbn).author("Fulano").title("As aventuras").build();
		entityManager.persist(book);
		
		
		// execucao
		boolean exists = repository.existsByIsbn(isbn);

		
		// verificação
		assertThat(exists).isTrue();
		
	}
	@Test
	@DisplayName("Deve retornar falso quando não existir o livro na base com o isbn informado.")
	public void shouldReturnTrueWhenIsbnDoesntExists() {
		
		// cenario
		String isbn = "123";		
		
		// execucao
		boolean exists = repository.existsByIsbn(isbn);

		// verificação
		assertThat(exists).isFalse();
		
	}
}
