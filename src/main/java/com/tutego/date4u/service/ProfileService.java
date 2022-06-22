package com.tutego.date4u.service;


import com.tutego.date4u.core.enities.Photo;
import com.tutego.date4u.core.enities.Unicorn;
import com.tutego.date4u.core.repository.ProfileRepository;
import com.tutego.date4u.core.enities.Profile;
import com.tutego.date4u.core.repository.UnicornRepository;
import com.tutego.date4u.service.formdata.ProfileEmailFormData;
import com.tutego.date4u.service.formdata.ProfileFormData;
import com.tutego.date4u.service.formdata.UnicornFormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Validated
public class ProfileService {

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    UnicornRepository unicornRepository;

    public long getNumberOfProfiles() {
        return profileRepository.count();
    }

    private Optional<ProfileFormData> transformProfileToFormData(Profile profile) {
        ProfileFormData formData = new ProfileFormData();
        formData.setBirthdate(profile.getBirthdate());
        formData.setDescription(profile.getDescription());
        formData.setAttractedToGender(profile.getAttractedToGender());
        formData.setGender(profile.getGender());
        formData.setNickname(profile.getNickname());
        formData.setHornlength(profile.getHornlength());
        formData.setLastseen(profile.getLastseen());
        return Optional.of(formData);
    }

    private Optional<ProfileEmailFormData> transformProfileToFormDataWithEmail(Profile profile) {
        ProfileEmailFormData formData = new ProfileEmailFormData();
        formData.setBirthdate(profile.getBirthdate());
        formData.setDescription(profile.getDescription());
        formData.setAttractedToGender(profile.getAttractedToGender());
        formData.setGender(profile.getGender());
        formData.setNickname(profile.getNickname());
        formData.setHornlength(profile.getHornlength());
        formData.setLastseen(profile.getLastseen());
        formData.setEmail(profile.getUnicorn().getEmail());
        List<String> photos = new ArrayList<>();
        for (Photo p : profile.getPhotos()) {
            photos.add(p.name);
        }
        formData.setPhotos(photos);
        List<String> nicknames = new ArrayList<>();
        for (Profile p : profile.getProfilesILike()) {
            nicknames.add(p.getNickname());
        }
        formData.setLikesNicknames(nicknames);
        return Optional.of(formData);
    }

    public Optional<ProfileFormData> getProfileByEmail(String email) {
        return profileRepository.findProfileByEmail(email)
                .flatMap(this::transformProfileToFormData);
    }

    public Optional<ProfileEmailFormData> getProfileWithEmailByEmail(String email) {
        return profileRepository.findProfileByEmail(email)
                .flatMap(this::transformProfileToFormDataWithEmail);
    }

    public Optional<ProfileFormData> getProfileByNickname(String nickname) {
        return profileRepository.findProfileByNickname(nickname)
                .flatMap(this::transformProfileToFormData);
    }

    public Optional<ProfileEmailFormData> getProfileWithEmailByNickname(String nickname) {
        return profileRepository.findProfileByNickname(nickname)
                .flatMap(this::transformProfileToFormDataWithEmail);
    }

    public Optional<String> getProfileNicknameByEmail(String email) {
        return profileRepository.findProfileByEmail(email).map(Profile::getNickname);
    }

    public boolean checkLikeProfile(String userEmail, String otherNickname) {

        return profileRepository.findProfileByEmail(userEmail)
                .map(userProfile -> profileRepository
                        .findProfileByNickname(otherNickname)
                        .map(otherProfile -> {
                            if (userProfile.getProfilesILike().contains(otherProfile)) {
                                userProfile.getProfilesILike().remove(otherProfile);
                            } else {
                                userProfile.getProfilesILike().add(otherProfile);
                            }
                            profileRepository.save(userProfile);
                            return true;
                        })
                        .orElse(false))
                .orElse(false);
    }

    public Optional<List<ProfileFormData>> getProfiles(String user, Integer startYear, Integer endYear,
                                                       Integer startLength, Integer endLength,
                                                       int gender) {
        LocalDate currentDate = LocalDate.now();
        Byte attractedTo;
        attractedTo = switch (gender) {
            case 0 -> Byte.valueOf("0");
            case 1 -> Byte.valueOf("1");
            case 2 -> Byte.valueOf("2");
            default -> null;
        };
        Optional<List<Profile>> profilesList = getProfileByEmail(user)
                .map(profile -> profileRepository.search(
                        Byte.parseByte(String.valueOf(profile.getGender())),
                        attractedTo,
                        currentDate.minusYears(endYear),
                        currentDate.minusYears(startYear),
                        startLength.shortValue(),
                        endLength.shortValue()));
        Optional<List<ProfileFormData>> profilesDTOList = Optional.of(new ArrayList<>());
        profilesList.ifPresent(list -> {
            for (Profile p :
                    list) {
                profilesDTOList.get().add(new ProfileFormData(p.getNickname(), p.getBirthdate(), p.getHornlength(), p.getGender(), p.getAttractedToGender(), p.getDescription(), p.getLastseen()));
            }
        });
        return profilesDTOList;

    }

    public Optional<List<ProfileFormData>> getProfiles(String user, Integer startYear, Integer endYear,
                                                       Integer startLength, Integer endLength) {
        LocalDate currentDate = LocalDate.now();
        Optional<List<ProfileFormData>> profilesDTOList = Optional.of(new ArrayList<>());
        for (int genderID = 0; genderID < 3; genderID++) {
            final int gender = genderID;
            Optional<List<Profile>> profilesList = getProfileByEmail(user)
                    .map(profile -> profileRepository.search(
                            Byte.parseByte(String.valueOf(profile.getGender())),
                            Byte.parseByte(Integer.toString(gender)),
                            currentDate.minusYears(endYear),
                            currentDate.minusYears(startYear),
                            startLength.shortValue(),
                            endLength.shortValue()));

            profilesList.ifPresent(list -> {
                for (Profile p : list) {
                    profilesDTOList
                            .get()
                            .add(new ProfileFormData(
                                            p.getNickname(),
                                            p.getBirthdate(),
                                            p.getHornlength(),
                                            p.getGender(),
                                            p.getAttractedToGender(),
                                            p.getDescription(),
                                            p.getLastseen()
                                    )
                            );
                }
            });
        }
        return profilesDTOList;
    }

    public void createNewUser(UnicornFormData unicornData){
        Unicorn newUnicorn = new Unicorn();
        newUnicorn.setPassword("{noop}" + unicornData.getPassword());
        newUnicorn.setEmail(unicornData.getEmail());

        Profile profile = new Profile("", LocalDate.now(), 0, 0, null, "", LocalDateTime.now());
        newUnicorn.setProfile(profile);
        profileRepository.save(profile);
        unicornRepository.save(newUnicorn);
    }

    //TODO
    public Optional<Boolean> checkLike(String nickname) {
        return Optional.of(true);
    }

    public String updateProfile(String userEmail, ProfileFormData profileFormData){
       return profileRepository.findProfileByEmail(userEmail).map(profile ->{
           profileRepository
                   .updateProfile(
                           profile.getId(),
                           profileFormData.getNickname(),
                           profileFormData.getBirthdate(),
                           (short)profileFormData.getHornlength(),
                           (byte)profileFormData.getGender(),
                           profileFormData.getAttractedToGender().byteValue(),
                           profileFormData.getDescription(),
                           profileFormData.getLastseen()
                   );
           return profileFormData.getNickname();
        }).orElse(null);
    }
}
