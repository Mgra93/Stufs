using RWAEvent.BL.Models.DTO.LogEntry;
using RWAEvent.BL.Models.Enums;

namespace RWAEvent.BL.Services
{
    public interface IEventLogger
    {
        Task<int> GetLogsCount();
        Task<List<LogEntryDTO>> GetLastLogs(int n);
        void Log(LogLvl level, string message);
    }
}
