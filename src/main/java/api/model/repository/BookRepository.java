package api.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import api.model.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long>{

	boolean existsByIsbn(String isbn);

}
