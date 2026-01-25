package com.rescuebites.api.commerce.controllers.interfaces;

import com.rescuebites.api.commerce.controllers.requests.CreateCommerceRequest;
import com.rescuebites.api.commerce.controllers.requests.UpdateCommerceRequest;
import com.rescuebites.api.commerce.controllers.responses.CommerceResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RequestMapping("/api/v1/commerces")
public interface ICommerceController {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(CREATED)
    void createCommerce(@RequestPart("commerce") @Valid CreateCommerceRequest createCommerceRequest,
                        @RequestPart("images") MultipartFile[] images);

    @GetMapping("/{commerceId}")
    @ResponseStatus(OK)
    CommerceResponse getCommerceById(@PathVariable UUID commerceId);

    @PatchMapping(value = "/{commerceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(OK)
    void updateCommerce(@PathVariable("commerceId") UUID commerceId,
                        @RequestPart("commerce") @Valid UpdateCommerceRequest updateCommerceRequest,
                        @RequestPart(value = "images", required = false) MultipartFile[] images);

    @DeleteMapping("/{commerceId}")
    @ResponseStatus(NO_CONTENT)
    void deleteCommerce(@PathVariable UUID commerceId);
}