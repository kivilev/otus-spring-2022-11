package org.kivilev.service;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kivilev.dao.BookDao;
import org.kivilev.exception.ObjectNotFoundException;
import org.kivilev.model.Author;
import org.kivilev.model.Book;
import org.kivilev.model.BookFields;
import org.kivilev.model.Genre;
import org.kivilev.service.io.input.InputBookService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;

@SpringBootTest
class BookServiceImplTest {

    private static final Integer CREATED_YEAR = 2020;
    private static final String TITLE = "Название";
    private static final Long AUTHOR_ID = 1L;
    private static final Long GENRE_ID = 2L;
    private static final String GENRE_NAME = "Жанр";
    private static final String AUTHOR_NAME = "Имя автора";
    private static final LocalDate BIRTHDAY = LocalDate.of(1900, 1, 2);
    private static final LocalDate DEATHDAY = LocalDate.of(2000, 3, 4);
    private static final long NOT_EXISTED_BOOK_ID = -1L;

    @Autowired
    private BookService bookService;
    @MockBean
    private GenreService genreService;
    @MockBean
    private AuthorService authorService;

    @MockBean
    private InputBookService inputBookService;

    @MockBean
    private BookDao bookDao;

    @Test
    @DisplayName("Создание книги с корректными параметрами происходит успешно")
    public void addingBookWithCorrectDataShouldBeSuccessful() {
        Map<BookFields, Object> fields = Map.of(BookFields.TITLE, TITLE, BookFields.CREATED_YEAR, CREATED_YEAR,
                BookFields.AUTHOR_ID, AUTHOR_ID, BookFields.GENRE_ID, GENRE_ID);
        Mockito.when(inputBookService.getNewBook()).thenReturn(fields);
        Mockito.when(genreService.getGenre(GENRE_ID)).thenReturn(new Genre(GENRE_ID, GENRE_NAME));
        Mockito.when(authorService.getAuthor(AUTHOR_ID)).thenReturn(new Author(AUTHOR_ID, AUTHOR_NAME, BIRTHDAY, DEATHDAY));

        bookService.addBook();

        Mockito.verify(bookDao, times(1)).save(argThat(
                book -> TITLE.equals(book.getTitle()) &&
                        Objects.equals(book.getCreatedYear(), CREATED_YEAR) &&
                        isCorrectGenre(book) &&
                        isCorrectAuthor(book)
        ));
    }

    @Test
    @DisplayName("Создание книги с несуществующим автором должно завершаться ошибкой")
    public void addingBookWithNonExistedAuthorIdShouldThrowException() {
        Map<BookFields, Object> fields = Map.of(BookFields.TITLE, TITLE, BookFields.CREATED_YEAR, CREATED_YEAR,
                BookFields.AUTHOR_ID, AUTHOR_ID, BookFields.GENRE_ID, GENRE_ID);
        Mockito.when(inputBookService.getNewBook()).thenReturn(fields);
        Mockito.when(genreService.getGenre(GENRE_ID)).thenThrow(ObjectNotFoundException.class);
        Mockito.when(authorService.getAuthor(AUTHOR_ID)).thenReturn(new Author(AUTHOR_ID, AUTHOR_NAME, BIRTHDAY, DEATHDAY));

        assertThrows(ObjectNotFoundException.class, () -> {
            bookService.addBook();
        });
    }

    @Test
    @DisplayName("Создание книги с несуществующим жанром должно завершаться ошибкой")
    public void addingBookWithNonExistedGenreIdShouldThrowException() {
        Map<BookFields, Object> fields = Map.of(BookFields.TITLE, TITLE, BookFields.CREATED_YEAR, CREATED_YEAR,
                BookFields.AUTHOR_ID, AUTHOR_ID, BookFields.GENRE_ID, GENRE_ID);
        Mockito.when(inputBookService.getNewBook()).thenReturn(fields);
        Mockito.when(genreService.getGenre(GENRE_ID)).thenReturn(new Genre(GENRE_ID, GENRE_NAME));
        Mockito.when(authorService.getAuthor(AUTHOR_ID)).thenThrow(ObjectNotFoundException.class);

        assertThrows(ObjectNotFoundException.class, () -> {
            bookService.addBook();
        });
    }

    @Test
    @DisplayName("Удаление несуществующей книги не должно завершаться ошибкой")
    public void deleteNotExistedBookShouldNotThrowException() {
        Mockito.when(inputBookService.getRemoveId()).thenReturn(NOT_EXISTED_BOOK_ID);

        bookService.removeBook();

        Mockito.verify(bookDao, times(1)).delete(NOT_EXISTED_BOOK_ID);
    }

    @Test
    @DisplayName("Изменение заголовка несуществующей книги не должно завершаться ошибкой")
    public void changeTitleNotExistedBookShouldNotThrowException() {
        String newTitle = "новое название";
        Mockito.when(inputBookService.getNewTitle()).thenReturn(Pair.of(NOT_EXISTED_BOOK_ID, newTitle));

        bookService.updateBookTitle();

        Mockito.verify(bookDao, times(1)).updateTitle(NOT_EXISTED_BOOK_ID, newTitle);
    }

    private boolean isCorrectAuthor(Book book) {
        return Objects.equals(book.getAuthor().getId(), AUTHOR_ID) &&
                AUTHOR_NAME.equals(book.getAuthor().getName());
    }

    private boolean isCorrectGenre(Book book) {
        return Objects.equals(book.getGenre().getId(), GENRE_ID) &&
                GENRE_NAME.equals(book.getGenre().getName());
    }
}