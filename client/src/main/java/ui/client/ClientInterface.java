package ui.client;

import model.ClientResponse;

public interface ClientInterface {

    ClientResponse eval(String input);

    String help();
}
