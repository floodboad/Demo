package com.jvirriel.demo.backend.users;

import com.jvirriel.demo.backend.bus.Producer;
import com.jvirriel.demo.model.users.User;
import com.jvirriel.exception.CustomErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api")
public class RestApiController {

    private static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

    private final Producer producer;

    private final UserService userService;

    @Autowired
    public RestApiController(Producer producer, UserService userService) {
        this.producer = producer;
        this.userService = userService;
    }

    // -------------------Retrieve All Users---------------------------------------------

    @RequestMapping(value = "/user/", method = RequestMethod.GET)
    public ResponseEntity<List<User>> listAllUsers() {
        List<User> users = userService.findAllUsers();
        if (users.isEmpty()) {
            return new ResponseEntity<>(NO_CONTENT);
        }

        return new ResponseEntity<List<User>>(users, OK);
    }

    // -------------------Retrieve Single User------------------------------------------

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable("id") long id) {
        logger.info("Fetching User with id {}", id);


        User user = userService.findById(id);
        if (user == null) {
            logger.error("User with id {} not found.", id);
            return new ResponseEntity<>(CustomErrorType.get("User with id " + id
                    + " not found"), NOT_FOUND);
        }
        producer.send("topico1", user.toString());
        return new ResponseEntity<User>(user, OK);
    }

    // -------------------Create exceptions User-------------------------------------------

    @RequestMapping(value = "/user/", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
        logger.info("Creating User : {}", user);

        if (userService.isUserExist(user)) {
            logger.error("Unable to create. util User with name {} already exist", user.getName());
            return new ResponseEntity<>(CustomErrorType.get("Unable to create. util User with name " +
                    user.getName() + " already exist."), CONFLICT);
        }
        userService.saveUser(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/user/{id}").buildAndExpand(user.getId()).toUri());
        return new ResponseEntity<String>(headers, CREATED);
    }

    // ------------------- Update exceptions User ------------------------------------------------

    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable("id") long id, @RequestBody User user) {
        logger.info("Updating User with id {}", id);

        User currentUser = userService.findById(id);

        if (currentUser == null) {
            logger.error("Unable to update. User with id {} not found.", id);
            return new ResponseEntity<>(CustomErrorType.get("Unable to upate. User with id " + id + " not found."),
                    NOT_FOUND);
        }

        currentUser.setName(user.getName());
        currentUser.setAge(user.getAge());
        currentUser.setSalary(user.getSalary());

        userService.updateUser(currentUser);
        return new ResponseEntity<User>(currentUser, OK);
    }

    // ------------------- Delete exceptions User-----------------------------------------

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
        logger.info("Fetching & Deleting User with id {}", id);

        User user = userService.findById(id);
        if (user == null) {
            logger.error("Unable to delete. User with id {} not found.", id);
            return new ResponseEntity<>(CustomErrorType.get("Unable to delete. User with id " + id + " not found."),
                    NOT_FOUND);
        }
        userService.deleteUserById(id);
        return new ResponseEntity<User>(NO_CONTENT);
    }

    // ------------------- Delete All Users-----------------------------

    @RequestMapping(value = "/user/", method = RequestMethod.DELETE)
    public ResponseEntity<User> deleteAllUsers() {
        logger.info("Deleting All Users");

        userService.deleteAllUsers();
        return new ResponseEntity<User>(NO_CONTENT);
    }


    @PostMapping(
            value = "/user/publicer/{action}")
    @ResponseBody
    public ResponseEntity<String> publicerUserByProcess(
            @PathVariable("action") String action,
            HttpServletResponse response
    ) {
        logger.info("*** publicar: " + action + " ***");
        User user = new User();
        user.setId(Long.parseLong("Kafka"));
        user.setSalary(Double.parseDouble(action));
        producer.send("test7", user.toString());
        producer.send("test7", action);

        return new ResponseEntity<String>("Respuesta" + action, OK);
    }
}

