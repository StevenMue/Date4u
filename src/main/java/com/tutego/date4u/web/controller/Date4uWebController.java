package com.tutego.date4u.web.controller;


import com.tutego.date4u.service.PhotoService;
import com.tutego.date4u.service.ProfileService;
import com.tutego.date4u.service.SecurityService;
import com.tutego.date4u.service.UnicornService;
import com.tutego.date4u.service.formdata.ProfileFormData;
import com.tutego.date4u.service.formdata.UnicornFormData;
import com.tutego.date4u.core.repository.PhotoRepository;
import com.tutego.date4u.core.repository.UnicornRepository;
import com.tutego.date4u.core.enities.Profile;
import com.tutego.date4u.core.repository.ProfileRepository;
import com.tutego.date4u.core.enities.Unicorn;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;


@Controller
public class Date4uWebController {

    Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    UnicornRepository unicornRepository;
    @Autowired
    UnicornService unicornService;
    @Autowired
    ProfileRepository profiles;
    @Autowired
    ProfileService profileService;
    @Autowired
    PhotoRepository photoRepository;
    @Autowired
    PhotoService photoService;
    @Autowired
    SecurityService securityService;

    @RequestMapping("/*")
    public String indexPage(Model model) {
        model.addAttribute("totalProfiles", profileService.getNumberOfProfiles());
        model.addAttribute("loggedIn", securityService.getUserName());
        return "index";
    }

    //TODO MAYE NEW PROFILE USELESS?
    @RequestMapping("/profile")
    public String profilePage() {
        return profileService.getProfileNicknameByEmail(securityService.getUserName())
                .map(profileNickname -> "redirect:/profile/" + profileNickname)
                .orElse("redirect:/profile/newProfile");
    }

    //TODO Registration Test
    @RequestMapping("/profile/newProfile")
    public String newProfilePage(Model model) {
        profileService.getProfileByEmail(securityService.getUserName())
                .map(profile -> "redirect:/profile/" + profile)
                .orElse(null);
        Profile profile = new Profile("", null, 0, 0, null, null, null);
        model.addAttribute("profile",
                new ProfileFormData(
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
    public String searchPage(Model model, @RequestParam(value = "startYear", defaultValue = "18") Integer startYear, @RequestParam(value = "endYear", defaultValue = "100") Integer endYear,
                             @RequestParam(value = "startLength", defaultValue = "0") Integer startLength, @RequestParam(value = "endLength", defaultValue = "50") Integer endLength,
                             @RequestParam(value = "gender", defaultValue = "0") Integer gender, @RequestParam(value = "firstPage", defaultValue = "true") Boolean firstPage) {

        if (firstPage) {
            model.addAttribute("profile", profileService.getProfiles(securityService.getUserName(), startYear, endYear, startLength, endLength).orElse(null));
            return "search";
        } else {
            model.addAttribute("profile", profileService.getProfiles(securityService.getUserName(), startYear, endYear, startLength, endLength, gender).orElse(null));
            return "searchProfiles";
        }
    }

    //TODO nickname UNIQUE machen!
    @RequestMapping("user(nickname={nickname})/img/{picture}")
    public void showPicture(@PathVariable String nickname, @PathVariable String picture, HttpServletResponse response) {

        response.setContentType("image/jpg");
        photoService.getPictureByNicknameAndPhotoName(nickname, picture)
                .ifPresent(photo -> {
                    try (InputStream is = new ByteArrayInputStream(photo)) {
                        IOUtils.copy(is, response.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    @RequestMapping("user(nickname={nickname})/profilePicture")
    public void showProfilePicture(Model model, @PathVariable String nickname, HttpServletResponse response) {

        response.setContentType("image/jpg");
        photoService.getProfilePictureByNickname(nickname)
                .ifPresentOrElse(photo -> {
                    try (InputStream is = new ByteArrayInputStream(photo)) {
                        IOUtils.copy(is, response.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                },()-> log.error("Profile picture couldn't be found"));
    }

    @RequestMapping("/profile/{nickname}")
    public String profilePage(@PathVariable String nickname, Model model, @RequestParam(value = "updateSuccess", required = false)
    String updateSuccess, @RequestParam(value = "errorFields", required = false) List<String> errorFields) {

        return profileService
                .getProfileWithEmailByNickname(nickname)
                .map(profile -> {

                    if (profile.getEmail().equals(securityService.getUserName())) {
                        model.addAttribute("otherProfile", false);
                    } else {
                        model.addAttribute("otherProfile", true);
                    }
                    if (updateSuccess != null) {
                        if (updateSuccess.equals("true")) {
                            model.addAttribute("change", true);
                        }
                        if (updateSuccess.equals("error")) {
                            model.addAttribute("change", false);
                            model.addAttribute("errorFields", errorFields);
                        }
                    }
                    profileService
                            .getProfileWithEmailByEmail(
                                    securityService
                                            .getUserName())
                            .ifPresent(
                                    (ownProfile -> {
                                        if (ownProfile.getLikesNicknames().contains(profile.getNickname())) {
                                            model.addAttribute("liked", true);
                                        } else {
                                            model.addAttribute("liked", false);
                                        }
                                    })
                            );

                    model.addAttribute("profileName", profile.getNickname());
                    model.addAttribute("allPictures", profile.getPhotos());
                    model.addAttribute("nickname", profile.getNickname());

                    model.addAttribute("profile",
                            new ProfileFormData(
                                    profile.getNickname(),
                                    profile.getBirthdate(),
                                    profile.getHornlength(),
                                    profile.getGender(),
                                    profile.getAttractedToGender(),
                                    profile.getDescription(),
                                    profile.getLastseen()
                            ));
                    return "profile";
                }).orElse("redirect:/");
    }

    @GetMapping("/user(id={nickname})/like")
    public String like(@PathVariable String nickname) {

        if (profileService.checkLikeProfile(securityService.getUserName(), nickname)) {
            return "redirect:/profile/" + nickname;
        } else {
            return null;
        }
    }

    @GetMapping("/img/{liked}")
    public void getLikeIcon(@PathVariable boolean liked, HttpServletResponse response) {
        photoService.getLikeImage(liked).ifPresent(image -> {
            try {
                InputStream is = new ByteArrayInputStream(image);
                IOUtils.copy(is, response.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("unicorn",
                new UnicornFormData(
                        "",
                        ""
                ));
        return "login";
    }

    @PostMapping("/regAction")
    public String registration(@ModelAttribute UnicornFormData unicornData) {
        profileService.createNewUser(unicornData);
        return "redirect:/login";
    }

    @PostMapping("/saveProfile")
    @Transactional
    public String saveProfile(@Valid @ModelAttribute ProfileFormData profileFormData, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errorFields = new StringBuilder();
            errorFields.append("&errorFields=");
            for (Object object : bindingResult.getAllErrors()) {
                if (object instanceof FieldError fieldError) {
                    errorFields.append((fieldError).getField()).append(",");
                }
            }
            errorFields.deleteCharAt(errorFields.lastIndexOf(","));
            return profileService
                    .getProfileByEmail(securityService
                            .getUserName())
                    .map(profile -> "redirect:/profile/" + profile.getNickname() + "?updateSuccess=error" + errorFields
            ).orElse(null);
        }
            return "redirect:/profile/" + profileService.updateProfile(securityService.getUserName(),profileFormData) + "?updateSuccess=true";
    }

    @GetMapping("/profile/index")
    public String profileBackToIndex() {
        return "redirect:/index";
    }

    @GetMapping("/profile/search")
    public String profileBackToSearch() {
        return "redirect:/search";
    }
}
