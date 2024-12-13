package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.Model.ApiUpdate;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.ApiUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ApiUpdateService {

    @Autowired
    private ApiUpdateRepository apiUpdateRepository;

    /**
     * Update or create ApiUpdate entry for a given user.
     */
    public void updateApiLastUpdated(int userId) {
        // Attempt to find the existing ApiUpdate for the user
        ApiUpdate apiUpdate = apiUpdateRepository.findByUser_id(userId);

        if (apiUpdate == null) {
            // Create new ApiUpdate if none exists
            apiUpdate = new ApiUpdate();
            User user = new User();
            user.setId(userId);
            apiUpdate.setUser(user);
        }

        // Update the lastUpdated field with the current time
        apiUpdate.setLastUpdated(LocalDateTime.now());

        // Save to the database
        apiUpdateRepository.save(apiUpdate);
    }
}

