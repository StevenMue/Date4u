package com.tutego.date4u.core.controller;


import com.tutego.date4u.core.service.PhotoService;
import com.tutego.date4u.dto.ProfileFormData;
import com.tutego.date4u.dto.SearchFormData;
import com.tutego.date4u.dto.UnicornFormData;
import com.tutego.date4u.repository.PhotoRepository;
import com.tutego.date4u.repository.UnicornRepository;
import com.tutego.date4u.repository.enities.Photo;
import com.tutego.date4u.repository.enities.Profile;
import com.tutego.date4u.repository.ProfileRepository;
import com.tutego.date4u.repository.enities.Unicorn;
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

import javax.servlet.http.HttpServletResponse;
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
    ProfileRepository profiles;

    @Autowired
    UnicornRepository unicornRepository;

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    PhotoService photoService;

    @RequestMapping("/*")
    public String indexPage(Model model) {
        log.info(Long.toString(profiles.count()));
        model.addAttribute("totalProfiles", profiles.count());
        model.addAttribute("loggedIn", SecurityContextHolder.getContext().getAuthentication().getName());
        return "index";
    }

    //TODO UPDATE!
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
    public String searchPage(Model model, @RequestParam(value = "startYear", defaultValue = "18") Integer startYear,@RequestParam(value = "endYear", defaultValue = "100") Integer endYear,
                             @RequestParam(value = "startLength", defaultValue = "0") Integer startLength, @RequestParam(value = "endLength" , defaultValue = "50") Integer endLength,
                             @RequestParam(value = "gender", defaultValue = "0") Integer gender, @RequestParam(value = "firstPage", defaultValue = "true") Boolean firstPage) {

        log.info("create Search DTO");
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();
        Unicorn user = unicornRepository.findUnicornsByEmail(principalName);

        if(startYear ==null &&endYear ==null && startLength==null &&endLength==null && gender == null) {
            model.addAttribute("filterData", null);
        }else{
            SearchFormData searchFormData = new SearchFormData();
            if(startYear!=null){
                searchFormData.setStartYear(startYear);
            }
            if(endYear!=null){
                searchFormData.setEndYear(endYear);
            }
            if(startLength!=null){
                searchFormData.setStartLength(startLength);
            }
            if(endLength!=null){
                searchFormData.setEndLength(endLength);
            }
            if(gender!=null){
                searchFormData.setGender(gender);
            }
            model.addAttribute("filterData", searchFormData);
        }
        Byte attractedTo;
        Byte myGender;
        LocalDate currentDate = LocalDate.now();
        if (gender == null) {
            attractedTo = null;
            myGender = null;
        } else {
            attractedTo = switch (gender) {
                case 0 -> Byte.valueOf("0");
                case 1 -> Byte.valueOf("1");
                case 2 -> Byte.valueOf("2");
                default -> null;
            };
            myGender = switch (user.getProfile().getGender()) {
                case 0 -> Byte.valueOf("0");
                case 1 -> Byte.valueOf("1");
                case 2 -> Byte.valueOf("2");
                default -> null;
            };
        }

        List<Profile> pro = profiles.search(myGender,attractedTo,currentDate.minusYears(endYear),currentDate.minusYears(startYear),startLength.shortValue(),endLength.shortValue());
        log.info("ALL VALID PROFILES");
        log.info(attractedTo+" "+myGender+" "+currentDate.minusYears(startYear)+" "+currentDate.minusYears(endYear)+" "+startLength.shortValue()+" "+endLength.shortValue());
        if (pro==null){
            log.error("NO ONE WAS FOUND");
        }
        for (Profile p: pro) {
            log.info("Profile: "+p.toString());
        }
        //TODO search the right profiles
        log.info(currentDate.minusYears(endYear)+" , "+currentDate.minusYears(startYear));

        if(firstPage) {
            pro.clear();
            pro.addAll(profiles.search((byte) 0,attractedTo,currentDate.minusYears(endYear),currentDate.minusYears(startYear),startLength.shortValue(),endLength.shortValue()));
            pro.addAll(profiles.search((byte) 1,attractedTo,currentDate.minusYears(endYear),currentDate.minusYears(startYear),startLength.shortValue(),endLength.shortValue()));
            pro.addAll(profiles.search((byte) 2,attractedTo,currentDate.minusYears(endYear),currentDate.minusYears(startYear),startLength.shortValue(),endLength.shortValue()));
            model.addAttribute("profile", pro);
            return "search";
        }else{
            model.addAttribute("profile", profiles.search(myGender,attractedTo,currentDate.minusYears(endYear),currentDate.minusYears(startYear),startLength.shortValue(),endLength.shortValue()));
            return "searchProfiles";
        }
    }

    @PostMapping("/filter")
    public String searchPageFilter(@ModelAttribute SearchFormData filter) {
        //TODO


        return "search";
    }

    @RequestMapping("user(id={id})/img/{picture}")
    public void showPicture( @PathVariable long id, @PathVariable String picture, HttpServletResponse response){
        log.info("Try to get Picture");

        response.setContentType("image/jpg");
        Optional<Profile> profile = profiles.findById(id);
        profile.ifPresentOrElse((profilePic) ->{
            Set<Photo> photos = profilePic.getPhotos();
            for (Photo photo: photos) {
                log.info(photo.name);
            }
            Photo singlePhoto = profilePic.getPhoto(picture);
            if(singlePhoto!=null) {
                Optional<byte[]> photo = photoService.download("./unicorns/img/" + singlePhoto.name);
                photo.ifPresentOrElse((pic) -> {
                    try {
                        InputStream is = new ByteArrayInputStream(pic);
                        IOUtils.copy(is, response.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, () -> {
                    log.error("No photo found with name: " + singlePhoto.name + " !");
                });
            }else{
                log.error("photo with the name: "+picture+" not found!");
            }
        }, () ->{
            log.error("Profile not found");
        });
    }

    @RequestMapping("user(id={id})/profilePicture")
    public void showProductImage(Model model, @PathVariable long id , HttpServletResponse response){
        log.info("Try to get the profile Picture with id: "+id);

        response.setContentType("image/jpg");
        Optional<Profile> profile = profiles.findById(id);
        profile.ifPresentOrElse((profilePic) ->{
            Photo profilePhoto = profilePic.getProfilePhoto();
            Optional<byte[]> photo = photoService.download("./unicorns/img/"+profilePhoto.name);
            photo.ifPresentOrElse((picture)->{ try{
                InputStream is = new ByteArrayInputStream(picture);
                IOUtils.copy(is, response.getOutputStream());
            }catch (IOException e){
                e.printStackTrace();
            }},()->{
                log.error("No profile photo found with id: "+id+" !");
            });
        }, () ->{
            log.error("Profile not found");
        });
    }

    @RequestMapping("/profile/{id}")
    public String profilePage(@PathVariable long id, Model model, @RequestParam(value = "updateSuccess", required = false)
    String updateSuccess, @RequestParam(value="errorFields", required = false) List<String> errorFields) {

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
            model.addAttribute("otherProfile", true);
        }else{
            model.addAttribute("otherProfile", false);
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
        if(user.getProfile().getProfilesILike().contains(maybeProfile.get())){
            model.addAttribute("liked",true);
        }else{
            model.addAttribute("liked",false);
        }

        Profile profile = maybeProfile.get();

            model.addAttribute("profileName", profile.getNickname());
            model.addAttribute("allPictures", profile.getPhotos());
            model.addAttribute("id", id);

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

    @GetMapping("/user(id={id})/like")
    public String like(@PathVariable long id){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String principalName = authentication.getName();
        Unicorn user = unicornRepository.findUnicornsByEmail(principalName);
        profiles.findById(id).ifPresentOrElse((prof) ->{
            Set<Profile> profileSet = user.getProfile().getProfilesILike();
            if(profileSet.contains(prof)){
                profileSet.remove(prof);
            }else{
                profileSet.add(prof);
            }
            profiles.save(user.getProfile());
        },()->{
            log.error("Profile not found! Can not be liked!");
        });
        return "redirect:/profile/"+id;
    }
    @GetMapping("/img/{liked}")
    public void getLikeIcon( @PathVariable boolean liked, HttpServletResponse response){
        Optional<byte[]> photo;
        if(liked) {
            Random r = new Random();
            int index = r.nextInt(1, 5);
            if(index<10) {
                photo = photoService.download("./webicon/liked_0" + index);
            }else{
                photo = photoService.download("./webicon/liked_" + index);
            }

        }else{
            photo = photoService.download("./webicon/liked_not");
        }
        photo.ifPresentOrElse((picture) -> {
            try {
                InputStream is = new ByteArrayInputStream(picture);
                IOUtils.copy(is, response.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, () -> {
            log.error("No profile webicon found with id: liked_" + " !");
        });
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
    @GetMapping("/profile/search")
    public String profileBackToSearch() {
        return "redirect:/search";
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
