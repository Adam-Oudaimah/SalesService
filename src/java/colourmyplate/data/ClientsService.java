package colourmyplate.data;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import colourmyplate.data.client.Client;
import colourmyplate.data.client.ClientRepository;
import colourmyplate.data.rest.server.v1.api.ClientsApi;
import colourmyplate.data.rest.server.v1.model.ClientDAO;

@RestController
@RequestMapping("/v1")
public class ClientsService implements ClientsApi {

	// The logger object
	private static Logger LOG = LoggerFactory.getLogger(ClientsService.class);

	@Autowired
	ClientRepository clientsRepository;

	@Override
	@Transactional
	public ResponseEntity<Void> createClient(@Valid ClientDAO clientDAO) {
		try {
			Client client = clientsRepository
					.save(new Client(null, clientDAO.getName(), clientDAO.getLastName(), clientDAO.getMobile()));
			if (client.getId() != null) {
				LOG.info("New client saved successfully");
				return new ResponseEntity<Void>(HttpStatus.OK);
			} else {
				LOG.error("Error while saving the new client.");
				return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOG.error(String.format("Error while saving the new client. ", e.getMessage()));
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<List<ClientDAO>> getClients() {
		try {
			// Final result
			List<ClientDAO> result = new ArrayList<ClientDAO>();

			// Query result
			List<Client> clients = clientsRepository.findAll();

			if (clients.isEmpty()) {
				return new ResponseEntity<List<ClientDAO>>(HttpStatus.NOT_FOUND);
			}

			// Loop over the query and fill in the final result
			for (Client client : clients) {
				ClientDAO clientDAO = new ClientDAO();
				clientDAO.setId(client.getId().toString());
				clientDAO.setName(client.getName());
				clientDAO.setLastName(client.getLastName());
				clientDAO.setMobile(client.getMobile());
				result.add(clientDAO);
			}

			LOG.info("All clients fetched successfully");
			return new ResponseEntity<List<ClientDAO>>(result, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error(String.format("Error while saving the new client. ", e.getMessage()));
			return new ResponseEntity<List<ClientDAO>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<Void> updateClient(@Valid ClientDAO clientDAO) {
		try {
			Client client = clientsRepository.findById(Long.parseLong(clientDAO.getId())).get();
			client.setName(client.getName());
			client.setLastName(client.getLastName());
			client.setMobile(client.getMobile());
			clientsRepository.saveAndFlush(client);
			LOG.info("New client saved successfully");
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch (Exception e) {
			LOG.error(String.format("Error while saving the new client. ", e.getMessage()));
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
