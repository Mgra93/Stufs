package hr.algebra.cugomat.mapper;

import hr.algebra.cugomat.dto.WorkerDTO;
import hr.algebra.cugomat.models.Worker;

import java.util.List;
import java.util.stream.Collectors;

public class WorkerMapper {
    public static WorkerDTO toDTO(Worker worker) {
        if (worker == null) return null;

        WorkerDTO dto = new WorkerDTO();
        dto.setId(worker.getId());
        dto.setUser(UserMapper.toDTO(worker.getUser()));
        dto.setClient(ClientMapper.toDTO(worker.getClient()));

        return dto;
    }

    public static List<WorkerDTO> toEntityList(List<Worker> objectList) {
        if (objectList == null) return null;

        return objectList.stream()
                .map(WorkerMapper::toDTO)
                .collect(Collectors.toList());
    }
}
