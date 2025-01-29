package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.Model.ApiUpdate;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ApiUpdateRepository;
import com.example.shelfsensebe.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApiUpdateService {

    @Autowired
    private ApiUpdateRepository apiUpdateRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Update or create ApiUpdate entry for a given user.
     */
    public void updateApiLastUpdated() {

        List<User> allUsers = userRepository.findAll();
        List<ApiUpdate> allApiUpdates = apiUpdateRepository.findAll();

        for (User user : allUsers) {
            ApiUpdate apiUpdate = apiUpdateRepository.findByUser_id(user.getId());

            if (apiUpdate == null) {
                // Create new ApiUpdate if none exists
                apiUpdate = new ApiUpdate();
                User newUser = new User();
                newUser.setId(user.getId());
                apiUpdate.setUser(newUser);
            }

            apiUpdate.setLastUpdated(LocalDateTime.now());
            allApiUpdates.add(apiUpdate);
        }

        apiUpdateRepository.saveAll(allApiUpdates);
    }
}

