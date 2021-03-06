package com.swuos.ALLFragment.library.libsearchs.search.model.bean;

/**
 * Created by 张孟尧 on 2016/9/3.
 */
public class SearchBookItem {
    //本次搜到的书的数量
    private int searchResultNum;

    public int getSearchResultNum() {
        return searchResultNum;
    }

    public void setSearchResultNum(int searchResultNum) {
        this.searchResultNum = searchResultNum;
    }

    // 图书ID
    private String bookId;
    //图书详情页URL
    private String bookDetailUrl;

    public String getBookDetailUrl() {
        return bookDetailUrl;
    }

    public void setBookDetailUrl(String bookDetailUrl) {
        this.bookDetailUrl = bookDetailUrl;
    }

    //    书名
    private String bookName;
    //    索书号
    private String bookSuoshuhao;
    private String ISBN;
    //    出版社
    private String publisher;
    //    摘要
    private String summary;
    //    作者
    private String writer;
    //    馆藏数
    private String bookNumber;
    //    可借数
    private String lendableNumber;
    //    馆藏地址
    private String address;
    //    在架
    private String frame;
    //    架位
    private String shelf;
    private boolean BookCover = false;
    private String bookCoverUrl;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookCoverUrl() {
        return bookCoverUrl;
    }

    public void setBookCoverUrl(String bookCoverUrl) {
        this.bookCoverUrl = bookCoverUrl;
    }

    public boolean isBookCover() {
        return BookCover;
    }

    public void setBookCover(boolean BookCover) {
        this.BookCover = BookCover;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAllInfo() {
        return String.format("%s\n%s\n%s\n%s\n", bookName, bookSuoshuhao, ISBN, publisher);
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookSuoshuhao() {
        return bookSuoshuhao;
    }

    public void setBookSuoshuhao(String bookSuoshuhao) {
        this.bookSuoshuhao = bookSuoshuhao;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(String bookNumber) {
        this.bookNumber = bookNumber;
    }

    public String getLendableNumber() {
        return lendableNumber;
    }

    public void setLendableNumber(String lendableNumber) {
        this.lendableNumber = lendableNumber;
    }
}
