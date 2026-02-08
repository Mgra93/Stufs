package hr.algebra.cugomatfx.mapper;

import hr.algebra.cugomatfx.dto.ClientDTO;
import hr.algebra.cugomatfx.dto.ClientUpdateDTO;
import hr.algebra.cugomatfx.models.Client;

public class ClientMapper {
    public static Client toEntity(ClientDTO dto) {
        if (dto == null) return null;

        Client client = new Client();
        client.setName(dto.getName());
        client.setCode(dto.getCode());
        client.setAddress(dto.getAddress());
        client.setPhone(dto.getPhone());
        client.setOib(dto.getOib());
        client.setActive(dto.getActive());
        client.setLicenseExpiryTime(dto.getLicenseExpiryTime());
        client.setLocationSecret(dto.getLocationSecret());
        client.setWebPage(dto.getWebPageUrl());
        client.setVipDayActive(dto.getVipDayActive());
        client.setVipDayCode(dto.getVipDayCode());
        client.setLocationSecret(dto.getLocationSecret());

        return client;
    }

    public static ClientUpdateDTO toUpdateDTO(Client object) {
        if (object == null) return null;

        ClientUpdateDTO updateDTO = new ClientUpdateDTO();
        updateDTO.setClientCode(object.getCode());
        updateDTO.setVipDayActive(object.getVipDayActive());
        updateDTO.setVipDayCode(object.getVipDayCode());
        updateDTO.setLocationSecret(object.getLocationSecret());

        return updateDTO;
    }
}
