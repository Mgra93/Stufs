using AutoMapper;
using Microsoft.EntityFrameworkCore;
using RWAEvent.BL.Models;
using RWAEvent.BL.Models.DTO.Performer;
using RWAEvent.BL.Models.Filters;

namespace RWAEvent.BL.Services
{
    public class PerformerService : IPerformer
    {

        private readonly RwaeventContext _context;
        private readonly IMapper _mapper;

        public PerformerService(RwaeventContext context, IMapper mapper)
        {
            _context = context;
            _mapper = mapper;
        }

        public async Task<List<PerformerDTO>> GetAllPerformers()
        {
            var performersList = await _context.Performers
                .Where(p => p.Active)
                .OrderBy(p => p.Name)
                .ToListAsync();

            return _mapper.Map<List<PerformerDTO>>(performersList);
        }

        public async Task<PerformerResultDTO> GetPerformersByFilter(PerformerFilter filter)
        {
            var query = _context.Performers
                .Where(p => p.Active)
                .AsQueryable();

            if (!string.IsNullOrWhiteSpace(filter.Search))
            {
                var searchTerm = filter.Search.Trim().ToLower();
                query = query.Where(p =>
                    p.Name.ToLower().Contains(searchTerm) ||
                    p.LastName.ToLower().Contains(searchTerm));
            }

            var total = await query.CountAsync();

            int skip = (filter.Page - 1) * filter.PageSize;

            var performerList = await query
                .OrderBy(p => p.Name)
                .ThenBy(p => p.LastName)
                .Skip(skip)
                .Take(filter.PageSize)
                .ToListAsync();

            var dtoList = _mapper.Map<List<PerformerDTO>>(performerList);

            return new PerformerResultDTO
            {
                TotalCount = total,
                Performers = dtoList
            };
        }


        public async Task<PerformerDTO?> GetPerformerById(int id)
        {
            var performer = await _context.Performers
                .FirstOrDefaultAsync(p => p.Id == id && p.Active);

            if (performer == null)
                return null;

            return _mapper.Map<PerformerDTO>(performer);
        }

        public async Task<int> CreatePerformer(PerformerDTO dto)
        {
            var performer = _mapper.Map<Performer>(dto);
            performer.CreatedOn = DateTime.Now;
            performer.Active = true;
            _context.Performers.Add(performer);
            await _context.SaveChangesAsync();

            return performer.Id;
        }

        public async Task<bool> UpdatePerformer(PerformerDTO dto)
        {
            var existingPerformer = await _context.Performers.FirstOrDefaultAsync(p => p.Id == dto.Id && p.Active);
            if (existingPerformer == null)
                return false;

            existingPerformer.Name = dto.Name;
            existingPerformer.LastName = dto.LastName;
            await _context.SaveChangesAsync();

            return true;
        }

        public async Task<bool> DeletePerformer(int id)
        {
            var performer = await _context.Performers
             .FirstOrDefaultAsync(p => p.Id == id && p.Active);

            if (performer == null)
            {
                return false;
            }

            performer.Active = false;
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> PerformersExist(List<int> performerIds)
        {
            var count = await _context.Performers
                .Where(p => performerIds.Contains(p.Id) && p.Active)
                .CountAsync();

            return count == performerIds.Count;
        }
    }
}
