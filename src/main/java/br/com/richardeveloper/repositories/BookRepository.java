package br.com.richardeveloper.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.richardeveloper.models.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{

	boolean existsByIsbn(String isbn);

	boolean existsByTitle(String title);

}
