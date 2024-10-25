package ma.hmzelidrissi.controllers;

import lombok.Setter;
import ma.hmzelidrissi.models.User;
import ma.hmzelidrissi.services.UserService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Setter
public class UserController implements Controller {
  private UserService userService;

  @Override
  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
    String path = request.getRequestURI();
    String method = request.getMethod();

    String contextPath = request.getContextPath();
    if (path.startsWith(contextPath)) {
      path = path.substring(contextPath.length());
    }

    try {

      if (path.equals("/users")) {
        if (method.equals("GET")) {
          return listUsers();
        } else if (method.equals("POST")) {
          return handleFormSubmission(request, response);
        }
      }

      if (path.equals("/users/new") && method.equals("GET")) {
        return showCreateForm();
      }

      if (path.equals("/users/search") && method.equals("GET")) {
        String query = request.getParameter("query");
        return searchUsers(query);
      }

      if (path.matches("/users/\\d+/edit") && method.equals("GET")) {
        Long id = extractIdFromPath(path);
        return showEditForm(id, response);
      }

      if (path.matches("/users/\\d+")) {
        Long id = extractIdFromPath(path);
        if (method.equals("POST")) {
          String methodOverride = request.getParameter("_method");
          if ("PUT".equalsIgnoreCase(methodOverride)) {
            return handleUpdateUser(id, request, response);
          } else if ("DELETE".equalsIgnoreCase(methodOverride)) {
            return handleDeleteUser(id, request, response);
          }
        }
      }

      return new ModelAndView("redirect:/users");

    } catch (Exception e) {
      return handleError(request, response, e);
    }
  }

  private ModelAndView listUsers() {
    try {
      List<User> users = userService.getAllUsers();
      ModelAndView mav = new ModelAndView("users/list");
      mav.addObject("users", users);
      return mav;
    } catch (Exception e) {
      throw new RuntimeException("Error fetching users: " + e.getMessage(), e);
    }
  }

  private ModelAndView showCreateForm() {
    ModelAndView mav = new ModelAndView("users/form");
    mav.addObject("user", new User());
    return mav;
  }

  private ModelAndView handleFormSubmission(
      HttpServletRequest request, HttpServletResponse response) {
    try {
      User user = new User();
      populateUserFromRequest(user, request);
      user = userService.saveUser(user);

      if (isAjaxRequest(request)) {
        response.setStatus(HttpServletResponse.SC_OK);
        return getAjaxSuccessResponse(user);
      }

      return new ModelAndView("redirect:/users");
    } catch (Exception e) {
      return handleError(request, response, e);
    }
  }

  private ModelAndView showEditForm(Long id, HttpServletResponse response) {
    try {
      User user = userService.getUserById(id);
      if (user == null) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return new ModelAndView("error :: message").addObject("error", "User not found");
      }

      ModelAndView mav = new ModelAndView("users/form");
      mav.addObject("user", user);
      return mav;
    } catch (Exception e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return new ModelAndView("error :: message")
          .addObject("error", "Error loading user: " + e.getMessage());
    }
  }

  private ModelAndView handleUpdateUser(
      Long id, HttpServletRequest request, HttpServletResponse response) {
    try {
      User existingUser = userService.getUserById(id);
      if (existingUser == null) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return new ModelAndView("error :: message").addObject("error", "User not found");
      }

      populateUserFromRequest(existingUser, request);
      existingUser = userService.saveUser(existingUser);

      if (isAjaxRequest(request)) {
        response.setStatus(HttpServletResponse.SC_OK);
        return getAjaxSuccessResponse(existingUser);
      }

      return new ModelAndView("redirect:/users");
    } catch (Exception e) {
      return handleError(request, response, e);
    }
  }

  private ModelAndView handleDeleteUser(
      Long id, HttpServletRequest request, HttpServletResponse response) {
    try {
      User user = userService.getUserById(id);
      if (user == null) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return new ModelAndView("error :: message").addObject("error", "User not found");
      }

      userService.deleteUser(id);

      if (isAjaxRequest(request)) {
        response.setStatus(HttpServletResponse.SC_OK);
        return new ModelAndView("users/list :: userList")
            .addObject("users", userService.getAllUsers());
      }

      return new ModelAndView("redirect:/users");
    } catch (Exception e) {
      return handleError(request, response, e);
    }
  }

  private ModelAndView searchUsers(String query) {
    try {
      List<User> users = userService.searchUsers(query);
      return new ModelAndView("users/list :: userList").addObject("users", users);
    } catch (Exception e) {
      throw new RuntimeException("Error searching users: " + e.getMessage(), e);
    }
  }

  private void populateUserFromRequest(User user, HttpServletRequest request) {
    try {

      String firstName = getRequiredParameter(request, "firstName", "First name");
      String lastName = getRequiredParameter(request, "lastName", "Last name");
      String identificationNumber =
          getRequiredParameter(request, "identificationNumber", "Identification number");
      String nationality = getRequiredParameter(request, "nationality", "Nationality");

      user.setFirstName(firstName.trim());
      user.setLastName(lastName.trim());
      user.setIdentificationNumber(identificationNumber.trim());
      user.setNationality(nationality.trim());

      String registrationDate = request.getParameter("registrationDate");
      String expirationDate = request.getParameter("expirationDate");

      if (registrationDate != null && !registrationDate.trim().isEmpty()) {
        try {
          user.setRegistrationDate(LocalDate.parse(registrationDate));
        } catch (DateTimeParseException e) {
          throw new IllegalArgumentException("Invalid registration date format");
        }
      }

      if (expirationDate != null && !expirationDate.trim().isEmpty()) {
        try {
          user.setExpirationDate(LocalDate.parse(expirationDate));
        } catch (DateTimeParseException e) {
          throw new IllegalArgumentException("Invalid expiration date format");
        }
      }

      if (user.getRegistrationDate() != null
          && user.getExpirationDate() != null
          && user.getExpirationDate().isBefore(user.getRegistrationDate())) {
        throw new IllegalArgumentException("Expiration date cannot be before registration date");
      }

    } catch (Exception e) {
      throw new IllegalArgumentException("Error processing form data: " + e.getMessage());
    }
  }

  private String getRequiredParameter(
      HttpServletRequest request, String paramName, String fieldName) {
    String value = request.getParameter(paramName);
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException(fieldName + " is required");
    }
    return value;
  }

  private ModelAndView getAjaxSuccessResponse(User user) {
    ModelAndView mav = new ModelAndView("users/list :: userList");
    mav.addObject("users", userService.getAllUsers());
    return mav;
  }

  private ModelAndView handleError(
      HttpServletRequest request, HttpServletResponse response, Exception e) {
    String errorMessage = e.getMessage();
    if (errorMessage == null || errorMessage.isEmpty()) {
      errorMessage = "An unexpected error occurred";
    }

    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

    if (isAjaxRequest(request)) {
      return new ModelAndView("error :: message").addObject("error", errorMessage);
    }

    ModelAndView mav = new ModelAndView("users/list");
    mav.addObject("error", errorMessage);
    mav.addObject("users", userService.getAllUsers());
    return mav;
  }

  private boolean isAjaxRequest(HttpServletRequest request) {
    return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
        || "true".equals(request.getHeader("HX-Request"));
  }

  private Long extractIdFromPath(String path) {
    try {
      return Long.parseLong(path.replaceAll(".*/([0-9]+).*", "$1"));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid user ID in path: " + path);
    }
  }
}
