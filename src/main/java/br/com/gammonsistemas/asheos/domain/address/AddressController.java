package br.com.gammonsistemas.asheos.domain.address;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gammonsistemas.asheos.domain.address.dto.AddressRequest;
import br.com.gammonsistemas.asheos.domain.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final UserService userService;

    /**
     * Cria um novo endereço para o usuário autenticado.
     */
    @PostMapping
    public ResponseEntity<Address> postAddress(
            @Valid @RequestBody AddressRequest request,
            Authentication authentication) {
        Long userId = handleLoggedUserId(authentication);
        Address address = addressService.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }

    /**
     * Lista todos os endereços do usuário autenticado.
     */
    @GetMapping
    public ResponseEntity<List<Address>> getAddressesByUser(Authentication authentication) {
        Long userId = handleLoggedUserId(authentication);
        List<Address> addresses = addressService.listUserAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Atualiza um endereço específico do usuário autenticado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Address> putAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request,
            Authentication authentication) {
        Long userId = handleLoggedUserId(authentication);
        Address updatedAddress = addressService.update(id, request, userId);
        return ResponseEntity.ok(updatedAddress);
    }

    /**
     * Deleta um endereço específico do usuário autenticado.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = handleLoggedUserId(authentication);
        addressService.delete(id, userId);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }

    private Long handleLoggedUserId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userService.findByEmail(userDetails.getUsername()).getId();
    }
}
