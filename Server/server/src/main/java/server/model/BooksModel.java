package server.model;

public class BooksModel {
	int id;
	String bookName, authorName, tableName, state, floor;

	public String getState() {
		return state;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
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
