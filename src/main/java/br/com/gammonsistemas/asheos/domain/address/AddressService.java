package br.com.gammonsistemas.asheos.domain.address;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import br.com.gammonsistemas.asheos.domain.address.dto.AddressRequest;
import br.com.gammonsistemas.asheos.domain.user.User;
import br.com.gammonsistemas.asheos.domain.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;

    public List<Address> listUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    public Address findById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Endereço não encontrado."));
    }

    public Address create(AddressRequest request, Long userId) {
        User user = userService.findById(userId);

        Address address = new Address();
        address.setUser(user);
        address.setStreet(request.street());
        address.setCity(request.city());
        address.setState(request.state());
        address.setZipCode(request.zipCode());

        return addressRepository.save(address);
    }

    public Address update(Long addressId, AddressRequest request, Long userId) {
        Address address = findById(addressId);

        // Autorização: O usuário logado é o dono do endereço?
        handleAddressUser(address, userId);

        // Atualiza os campos
        address.setStreet(request.street());
        address.setCity(request.city());
        address.setState(request.state());
        address.setZipCode(request.zipCode());

        return addressRepository.save(address);
    }

    public void delete(Long addressId, Long userId) {
        Address address = findById(addressId);

        // Autorização: O usuário logado é o dono do endereço?
        handleAddressUser(address, userId);

        addressRepository.delete(address);
    }

    /**
     * Verifica se o usuário autenticado é o dono da ocorrência.
     * 
     * @param occurrence
     * @param userId
     */
    private void handleAddressUser(Address address, Long userId) {
        if (!address.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Usuário não autorizado a modificar este endereço.");
        }
    }
}
