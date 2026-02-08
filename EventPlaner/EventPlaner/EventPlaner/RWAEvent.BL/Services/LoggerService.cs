using RWAEvent.BL.Models;
using Microsoft.EntityFrameworkCore;
using RWAEvent.BL.Models.Enums;
using RWAEvent.BL.Models.DTO.LogEntry;
using AutoMapper;

namespace RWAEvent.BL.Services
{
    public class LoggerService : IEventLogger
    {
        private readonly RwaeventContext _context;
        private readonly IMapper _mapper;

        public LoggerService(RwaeventContext context, IMapper mapper)
        {
            _context = context;
            _mapper = mapper;
        }

        public void Log(LogLvl level, string message)
        {
            var logEntry = new LogEntry
            {
                CreatedOn = DateTime.Now,
                Level = (int)level,
                Message = message
            };

            _context.LogEntries.Add(logEntry);
            _context.SaveChanges();
        }

        public async Task<int> GetLogsCount()
        {
            return await _context.LogEntries.CountAsync();
        }

        public async Task<List<LogEntryDTO>> GetLastLogs(int n)
        {
            var logList = await _context.LogEntries
                .OrderByDescending(log => log.CreatedOn)
                .Take(n)
                .ToListAsync();
            return _mapper.Map<List<LogEntryDTO>>(logList);
        }
    }
}
