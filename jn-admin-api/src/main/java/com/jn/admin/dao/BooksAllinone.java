package com.jn.admin.dao;

import com.jn.db.domain.JnBooks;
import com.jn.db.domain.JnBooksAttribute;
import com.jn.db.domain.JnBooksProduct;
import com.jn.db.domain.JnBooksSpecification;

/*
* 对应books的三个表
* */

public class BooksAllinone {
	JnBooks books;
	JnBooksSpecification[] specifications;
	JnBooksAttribute[] attributes;
	// 这里采用 Product 再转换到 JnBooksProduct
	JnBooksProduct[] products;

	public JnBooks getBooks() {
		return books;
	}

	public void setBooks(JnBooks books) {
		this.books = books;
	}

	public JnBooksProduct[] getProducts() {
		return products;
	}

	public void setProducts(JnBooksProduct[] products) {
		this.products = products;
	}

	public JnBooksSpecification[] getSpecifications() {
		return specifications;
	}

	public void setSpecifications(JnBooksSpecification[] specifications) {
		this.specifications = specifications;
	}

	public JnBooksAttribute[] getAttributes() {
		return attributes;
	}

	public void setAttributes(JnBooksAttribute[] attributes) {
		this.attributes = attributes;
	}

}
