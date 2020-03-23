package server.model;

public class BooksModel {
	int id;
	String bookName, authorName, tableName, state;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getId() {
		return id;
	}

	public String getBookName() {
		return bookName;
	}

	public String getAuthorName() {
		return authorName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
