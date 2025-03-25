package ru.cmc.msu.web_prak_2025.Controllers;

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
        return clientDetails.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Client>> getClientsByFilter(
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) ClientType type) {

        List<Client> clients;
        ClientDAO.Filter f = new ClientDAO.Filter(firstName, lastName, type);
        clients = clientDAO.getByFilter(f);

        return ResponseEntity.ok(clients);
    }

    @Transactional
    @RequestMapping(value = "/add_new_client", method = RequestMethod.POST)
    public String addClient(@RequestBody ClientInfo clientInfo) {
        try {
            if (clientInfo.getType() == null || clientInfo.getType().trim().isEmpty()) {
                throw new IllegalArgumentException("Client type cannot be null or empty");
            }

            ClientType clientType = ClientType.valueOf(clientInfo.getType());
            Client newClient = new Client(clientInfo.getFirstName(), clientInfo.getLastName(), clientInfo.getContacts(), clientType);

            clientDAO.save(newClient);

            return "redirect:/client?client_id=" + newClient.getId().toString();
        } catch (IllegalArgumentException e) {
            return "redirect:/error?message=" + e.getMessage();
        } catch (Exception e) {
            return "redirect:/error?message=An unexpected error occurred";
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ClientInfo {
        private String firstName;
        private String lastName;
        private String contacts;
        private String type;
    }

    @RequestMapping(value = "/{client_id}", method = RequestMethod.PUT)
    public String editClient(@PathVariable("client_id") Long client_id,
                             @RequestBody ClientInfo clientUpdateRequest) {
        Client client = clientDAO.getById(client_id);

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

        return "redirect:/client?client_id=" + client.getId().toString();
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