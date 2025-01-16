package gui.controllers;

import common.Subscriber;
import common.BookCopy;

public interface ItemLoader {
    void loadSubscriber(Subscriber subscriber);
    void loadBookCopy(BookCopy bookCopy);
}