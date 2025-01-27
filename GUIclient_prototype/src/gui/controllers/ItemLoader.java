package gui.controllers;

import common.Subscriber;
import common.BookCopy;

/**
 * This interface is used to load a Subscriber or BookCopy object into a controller.
 * It is used to decouple the controller from the Subscriber and BookCopy classes.
 */
public interface ItemLoader {
    /**
     * This method is used to load a Subscriber object into a controller.
     * @param subscriber
     */
    void loadSubscriber(Subscriber subscriber);

    /**
     * This method is used to load a BookCopy object into a controller.
     * @param bookCopy
     */
    void loadBookCopy(BookCopy bookCopy);
}