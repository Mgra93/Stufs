package hr.algebra.cugomatfx.mapper;

import hr.algebra.cugomatfx.dto.WorkerCreateDTO;
import hr.algebra.cugomatfx.dto.WorkerDTO;
import hr.algebra.cugomatfx.dto.WorkerUpdateDTO;
import hr.algebra.cugomatfx.models.Worker;

import java.util.List;
import java.util.stream.Collectors;

public class WorkerMapper {
    public static Worker toEntity(WorkerDTO dto) {
        if (dto == null) return null;

        Worker worker = new Worker();
        worker.setId(dto.getId());
        worker.setUser(UserMapper.toEntity(dto.getUser()));
        worker.setClient(ClientMapper.toEntity(dto.getClient()));

        return worker;
    }

    public static List<Worker> toEntityList(List<WorkerDTO> dtoList) {
        if (dtoList == null) return null;

        return dtoList.stream()
                .map(WorkerMapper::toEntity)
                .collect(Collectors.toList());
    }

    public static WorkerUpdateDTO toUpdateDTO(Worker object) {
        if (object == null)
            return null;

        WorkerUpdateDTO workerUpdateDTO = new WorkerUpdateDTO();
        workerUpdateDTO.setUsername(object.getUser().getUsername());
        workerUpdateDTO.setFirstName(object.getUser().getFirstName());
        workerUpdateDTO.setLastName(object.getUser().getLastName());
        workerUpdateDTO.setEmail(object.getUser().getEmail());
        workerUpdateDTO.setPassword(object.getUser().getPassword());

        return workerUpdateDTO;
    }
}
