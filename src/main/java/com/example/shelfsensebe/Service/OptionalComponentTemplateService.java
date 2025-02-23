package com.example.shelfsensebe.Service;

import com.example.shelfsensebe.DTO.UserDTO;
import com.example.shelfsensebe.Model.OptionalComponentTemplate;
import com.example.shelfsensebe.Model.OptionalComponentTemplateField;
import com.example.shelfsensebe.Model.User;
import com.example.shelfsensebe.Repository.OptionalComponentTemplateRepository;
import com.example.shelfsensebe.utility.TextSanitizer;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OptionalComponentTemplateService {

    @Autowired
    TextSanitizer textSanitizer;

    @Autowired
    private OptionalComponentTemplateRepository optionalComponentTemplateRepository;

    public OptionalComponentTemplate createOptionalComponentTemplate(OptionalComponentTemplate optionalComponentTemplate, UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        optionalComponentTemplate.setUser(user);

        optionalComponentTemplate.setName(textSanitizer.sanitize(optionalComponentTemplate.getName()));

        if (optionalComponentTemplate.getOptionalComponentTemplateFields() == null || optionalComponentTemplate.getOptionalComponentTemplateFields().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one field is required in the template.");
        }

        for (OptionalComponentTemplateField field : optionalComponentTemplate.getOptionalComponentTemplateFields()) {
            field.setName(textSanitizer.sanitize(field.getName()));
        }

        return optionalComponentTemplateRepository.save(optionalComponentTemplate);
    }

    public OptionalComponentTemplate updateOptionalComponentTemplate(int id, OptionalComponentTemplate updatedOptionalComponentTemplate) throws BadRequestException {
        OptionalComponentTemplate existingOptionalComponentTemplate = optionalComponentTemplateRepository.findById(id).orElseThrow(() ->
                new BadRequestException("Template Not Found")
        );

        existingOptionalComponentTemplate.setName(textSanitizer.sanitize(updatedOptionalComponentTemplate.getName()));

        if(updatedOptionalComponentTemplate.getOptionalComponentTemplateFields() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one field is required in the template.");
        }
        existingOptionalComponentTemplate.getOptionalComponentTemplateFields().clear();

        for (OptionalComponentTemplateField field : updatedOptionalComponentTemplate.getOptionalComponentTemplateFields()) {
            field.setOptionalComponentTemplate(existingOptionalComponentTemplate);
            field.setName(textSanitizer.sanitize(field.getName()));
            existingOptionalComponentTemplate.getOptionalComponentTemplateFields().add(field);
        }

        return optionalComponentTemplateRepository.save(existingOptionalComponentTemplate);

    }

    public void deleteOptionalComponentTemplate(int id) throws BadRequestException {
        OptionalComponentTemplate existingOptionalComponentTemplate = optionalComponentTemplateRepository.findById(id).orElseThrow(() ->
                new BadRequestException("Template Not Found")
        );
        optionalComponentTemplateRepository.delete(existingOptionalComponentTemplate);
    }
}
