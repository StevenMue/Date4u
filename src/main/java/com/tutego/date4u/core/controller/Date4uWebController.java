package com.tutego.date4u.core.controller;


import com.tutego.date4u.dto.ProfileFormData;
import com.tutego.date4u.dto.UnicornFormData;
import com.tutego.date4u.repository.UnicornRepository;
import com.tutego.date4u.repository.enities.Profile;
import com.tutego.date4u.repository.ProfileRepository;
import com.tutego.date4u.repository.enities.Unicorn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Controller
public class Date4uWebController {

    Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    ProfileRepository profiles;

    @Autowired
    UnicornRepository unicornRepository;

    @RequestMapping("/*")
    public String indexPage(Model model) {
        log.info(Long.toString(profiles.count()));
        model.addAttribute("totalProfiles", profiles.count());
        model.addAttribute("loggedIn", SecurityContextHolder.getContext().getAuthentication().getName());
        return "index";
    }

    @RequestMapping("/profile")
    public String profilePage() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();
        Unicorn user = unicornRepository.findUnicornsByEmail(principalName);
        if (user.getProfile() != null) {

            return "redirect:/profile/" + user.getProfile().getId();
        } else {
            return "redirect:/profile/newProfile";
        }
    }

    @RequestMapping("/profile/newProfile")
    public String newProfilePage(Model model) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();
        Unicorn user = unicornRepository.findUnicornsByEmail(principalName);
        if (user.getProfile() != null) {
            return "redirect:/profile/" + user.getProfile().getId();
        }
        Profile profile = new Profile("", null, 0, 0, null, null, null);
        model.addAttribute("profile",
                new ProfileFormData(profile.getId(),
                        profile.getNickname(),
                        profile.getBirthdate(),
                        profile.getHornlength(),
                        profile.getGender(),
                        profile.getAttractedToGender(),
                        profile.getDescription(),
                        profile.getLastseen()
                ));
        return "profile";
    }

    @RequestMapping("/search")
    public String searchPage(Model model) {

        model.addAttribute("profile", profiles.findAll());
        return "search";
    }

    @RequestMapping("/profile/{id}")
    public String profilePage(@PathVariable long id, Model model, @RequestParam(value = "updateSuccess", required = false) String updateSuccess, @RequestParam(value="errorFields", required = false) List<String> errorFields) {
        Optional<Profile> maybeProfile = profiles.findById(id);
        if (!maybeProfile.isPresent()) {
            log.error("Profile not present");
            return "redirect:/";
        }
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();
        Unicorn user = unicornRepository.findUnicornsByEmail(principalName);
        if (id != user.getProfile().getId()) {
            log.error("Profile not allowed to view"); //TODO change that if its not the same id -> read only
            return "redirect:/index";
        }
        if(updateSuccess!=null) {
            if (updateSuccess.equals("true")) {
                model.addAttribute("change", true);
            }
            if (updateSuccess.equals("error")) {
                model.addAttribute("change", false);
                model.addAttribute("errorFields", errorFields);
            }
        }
        Profile profile = maybeProfile.get();
        //public ProfileFormData(Long id, String nickname, LocalDate birthdate, short hornlength, byte gender, Byte attractedToGender, String description, LocalDateTime lastseen) {
        model.addAttribute("profile",
                new ProfileFormData(profile.getId(),
                        profile.getNickname(),
                        profile.getBirthdate(),
                        profile.getHornlength(),
                        profile.getGender(),
                        profile.getAttractedToGender(),
                        profile.getDescription(),
                        profile.getLastseen()
                ));
        return "profile";
    }

    @GetMapping("/login*")
    public String loginPage(Model model) {
        Unicorn unicorn = new Unicorn();
        //public UnicornFormData(Long id, String email, String password)
        model.addAttribute("unicorn",
                new UnicornFormData(unicorn.getId(),
                        unicorn.getEmail(),
                        unicorn.getPassword()
                ));
        return "login";
    }

    @GetMapping("/logout")
    public String logoutPage(Model model) {
        ;
        //public UnicornFormData(Long id, String email, String password)
        return "logout";
    }

    @PostMapping("/regAction")
    public String registration(@ModelAttribute UnicornFormData unicornData) {

        //public UnicornFormData(Long id, String email, String password)
        Optional<Unicorn> unicorn = Optional.ofNullable(unicornRepository.findUnicornsByEmail(unicornData.getEmail()));
        unicorn.ifPresentOrElse((unicorn1) -> {
                    log.error("User with email: " + unicorn1.getEmail() + " exists already"); //TODO make it visieble for user
                },
                () -> {
                    Unicorn newUnicorn = new Unicorn();
                    newUnicorn.setPassword("{noop}" + unicornData.getPassword());
                    newUnicorn.setEmail(unicornData.getEmail());
                    log.info("CREATED NEW USER");
                    Profile profile = new Profile("", LocalDate.now(), 0, 0, null, "", LocalDateTime.now());
                    newUnicorn.setProfile(profile);
                    log.info("BOUNDED PROFILE TO USER");
                    log.info("BEGIN UPDATE DATABASE...");
                    profiles.save(profile);
                    log.info("COMPLETE UPDATE DATABASE!");
                    log.info("BEGIN UPDATE DATABASE...");
                    unicornRepository.save(newUnicorn);
                    log.info("COMPLETE UPDATE DATABASE!");
                });
        return "redirect:/login";
    }

    @PostMapping("/saveProfile")
    @Transactional
    public String saveProfile(@Valid @ModelAttribute ProfileFormData profile, BindingResult bindingResult) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();
        Unicorn user = unicornRepository.findUnicornsByEmail(principalName);
        if (bindingResult.hasErrors()) {
            StringBuilder errorFields=new StringBuilder();
            errorFields.append("&errorFields=");
            for (Object object : bindingResult.getAllErrors()) {
                if (object instanceof FieldError fieldError) {
                    errorFields.append(((FieldError) object).getField()).append(",");
                }
            }
            errorFields.deleteCharAt(errorFields.lastIndexOf(","));
            return "redirect:/profile/" + user.getProfile().getId() + "?updateSuccess=error"+errorFields;
        }
        short hornlength = (short) profile.getHornlength();
        byte gender = (byte) profile.getGender();
        Byte attractedTo;
        if (profile.getAttractedToGender() == null) {
            attractedTo = null;
        } else {
            attractedTo = switch (profile.getAttractedToGender()) {
                case 0 -> Byte.valueOf("0");
                case 1 -> Byte.valueOf("1");
                case 2 -> Byte.valueOf("2");
                default -> null;
            };
        }
        if (user.getProfile() != null) {
            profiles.updateProfile(user.getProfile().getId(), profile.getNickname(), profile.getBirthdate(), hornlength,
                    gender, attractedTo, profile.getDescription(), profile.getLastseen());
            log.info(profile.toString());
            return "redirect:/profile/" + user.getProfile().getId() + "?updateSuccess=true";
        } else {
            return "redirect:/index"; //TODO if user is not logged in make a suitable error page appear
        }
    }

    @GetMapping("/profile/index")
    public String profileBackToIndex() {
        return "redirect:/index";
    }

    //TODO With this i can access the user
    // http://localhost:8080/authentication
    @GetMapping("/authentication")
    public String printPrincipalName() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();
        log.info(principalName);
        return principalName;
    }

    // http://localhost:8080/principal
    @GetMapping("/principal")
    public String currentUserName(Principal principal) {
        return principal.getName();
    }
}
