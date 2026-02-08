using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using RWAEvent.BL.Models.Enums;
using RWAEvent.BL.Services;

namespace RWAEvent.WebApi.Controllers
{
    [Route("api/logs")]
    [ApiController]
    public class LogsRestController : ControllerBase
    {
        private readonly IEventLogger _logger;

        public LogsRestController(IEventLogger logger)
        {
            _logger = logger;
        }

        [HttpGet("get/{n:int}")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> GetLastLogs(int n)
        {
            try
            {
                _logger.Log(LogLvl.Information, $"GetLastLogs called n:{n}");

                if (n <= 0)
                {
                    _logger.Log(LogLvl.Warning, $"Invalid parameter for GetLastLogs: n={n}");
                    return BadRequest("Number must be positive");
                }

                var logs = await _logger.GetLastLogs(n);
                return Ok(logs);
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error while getting logs");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }

        [HttpGet("count")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> GetLogsCount()
        {
            try
            {
                _logger.Log(LogLvl.Information, "GetLogsCount called.");
                var count = await _logger.GetLogsCount();
                _logger.Log(LogLvl.Information, $"Successfully retrieved logs count: {count}");
                return Ok(count);
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error in GetLogsCount");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }

        [ApiExplorerSettings(IgnoreApi = true)]
        [HttpGet("preview")]
        public IActionResult LogsPreview()
        {
            return Redirect("/pages/Logs.html");
        }
    }
}
