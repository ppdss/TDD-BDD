package api.model.repositoryTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import api.model.entity.Book;
import api.model.repository.BookRepository;

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
		Book book = createNewBook();
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
	@Test
	@DisplayName("Deve retornar livro por id.")
	public void findBookByIdTest() {
		//cenario
		Book book = createNewBook();
		entityManager.persist(book);

		//execução
		Optional<Book> foundBook = repository.findById(book.getId());

		assertThat( foundBook.isPresent() ).isTrue();
	}

	@Test
	@DisplayName("Deve salvar um livro.")
	public void saveBookTest() {
		//cenario
		Book book = createNewBook();
		Book savedBook = repository.save(book);

		//execução
		assertThat( savedBook.getId() ).isNotNull();
	}
	@Test
	@DisplayName("Deve deletar um livro")
	public void deleteBookTest() {
		//cenario
		Book book = createNewBook();
		entityManager.persist(book);

		Book foundBook = entityManager.find(Book.class, book.getId());
		
		//execução
		repository.delete(foundBook);
		
		//verificação
		Book deletedBook =  entityManager.find(Book.class, book.getId());
		assertThat(deletedBook).isNull();
		
	}

	public Book createNewBook() {
		return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
	}
}
