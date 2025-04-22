package ru.cmc.msu.web_prak_2025.Controllers;

import org.springframework.ui.Model;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cmc.msu.web_prak_2025.DAO.ClientDAO;
import ru.cmc.msu.web_prak_2025.DAO.impl.ClientDAOImpl;
import ru.cmc.msu.web_prak_2025.models.Client;
import ru.cmc.msu.web_prak_2025.models.Client.ClientType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private final ClientDAO clientDAO;

    @Autowired
    public ClientController(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }


    @GetMapping("/all")
    public ResponseEntity<Collection<Client>> getAllClients() {
        Collection<Client> clients = clientDAO.getAll();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        Optional<Client> client = Optional.ofNullable(clientDAO.getById(id));
        return client.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<String> getClientDetails(@PathVariable Long id) {
        Optional<String> clientDetails = clientDAO.getClientDetails(id);
        return clientDetails.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Client>> getClientsByFilter(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String type) {

        ClientType clientType = null;
        if (type != null && !type.isEmpty()) {
            try {
                clientType = ClientType.valueOf(type);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        ClientDAO.Filter f = new ClientDAO.Filter(firstName, lastName, clientType);
        List<Client> clients = clientDAO.getByFilter(f);

        return ResponseEntity.ok(clients);
    }

    @Transactional
    @PostMapping("/add_new_client")
    public ResponseEntity<Void> addClient(@RequestBody ClientInfo clientInfo) {
        try {
            ClientType clientType = ClientType.valueOf(clientInfo.getType());
            Client newClient = new Client(
                    clientInfo.getFirstName(),
                    clientInfo.getLastName(),
                    clientInfo.getContacts(),
                    clientType
            );

            clientDAO.save(newClient);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/{client_id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> editClient(@PathVariable("client_id") Long client_id,
                                           @RequestBody ClientInfo clientUpdateRequest) {
        Client client = clientDAO.getById(client_id);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }

        if (clientUpdateRequest.getFirstName() != null) {
            client.setFirstName(clientUpdateRequest.getFirstName());
        }
        if (clientUpdateRequest.getLastName() != null) {
            client.setLastName(clientUpdateRequest.getLastName());
        }
        if (clientUpdateRequest.getContacts() != null) {
            client.setContacts(clientUpdateRequest.getContacts());
        }
        if (clientUpdateRequest.getType() != null) {
            client.setType(ClientType.valueOf(clientUpdateRequest.getType()));
        }

        clientDAO.update(client);
        return ResponseEntity.ok().build();
    }

    @Getter
    @AllArgsConstructor
    public static class ClientInfo {
        private String firstName;
        private String lastName;
        private String contacts;
        private String type;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        Optional<Client> client = Optional.ofNullable(clientDAO.getById(id));
        if (client.isPresent()) {
            clientDAO.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}