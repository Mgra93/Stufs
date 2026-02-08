package hr.algebra.cugomat.mapper;

import hr.algebra.cugomat.dto.ClientDTO;
import hr.algebra.cugomat.dto.ClientPreviewDTO;
import hr.algebra.cugomat.models.Client;

public class ClientMapper {
    public static ClientDTO toDTO(Client client) {
        if (client == null) return null;

        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setCode(client.getCode());
        dto.setAddress(client.getAddress());
        dto.setPhone( client.getPhone());
        dto.setOib(client.getOib());
        dto.setActive(client.getActive());
        dto.setLicenseExpiryTime(client.getLicenseExpiryTime());
        dto.setLocationSecret(client.getLocationSecret());
        dto.setWebPageUrl(client.getWebPageUrl());
        dto.setVipDayActive(client.getVipDayActive());
        dto.setVipDayActive(client.getVipDayActive());
        dto.setVipDayCode(client.getVipDayCode());

        return dto;
    }

    public static ClientPreviewDTO toPreviewDTO(Client client) {
        if (client == null) return null;

        ClientPreviewDTO dto = new ClientPreviewDTO();
        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setCode(client.getCode());
        dto.setAddress(client.getAddress());
        dto.setPhone( client.getPhone());
        dto.setOib(client.getOib());
        dto.setLocationSecret(client.getLocationSecret());

        return dto;
    }

    public static Client toEntity(ClientDTO dto) {
        if (dto == null) return null;

        Client client = new Client();
        client.setName(dto.getName());
        client.setAddress(dto.getAddress());
        client.setPhone(dto.getPhone());
        client.setOib(dto.getOib());

        return client;
    }
}
