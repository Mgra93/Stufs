using RWAEvent.BL.Models.DTO.Performer;
using RWAEvent.BL.Models.Filters;

namespace RWAEvent.BL.Services
{
    public interface IPerformer
    {
        Task<List<PerformerDTO>> GetAllPerformers();
        Task<PerformerResultDTO> GetPerformersByFilter(PerformerFilter filter);
        Task<PerformerDTO?> GetPerformerById(int id);
        Task<int> CreatePerformer(PerformerDTO dto);
        Task<bool> UpdatePerformer(PerformerDTO dto);
        Task<bool> DeletePerformer(int id);
        Task<bool> PerformersExist(List<int> performerIds);
    }
}
