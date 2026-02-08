using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using RWAEvent.BL.Models.Filters;
using RWAEvent.BL.Services;
using RWAEvent.BL.Models.Enums;
using RWAEvent.BL.Models.DTO.Performer;

namespace RWAEvent.WebApi.Controllers
{
    [Route("api/performer")]
    [ApiController]
    public class PerformerRestController : ControllerBase
    {
        private readonly IEventLogger _logger;
        private readonly IPerformer _performerService;

        public PerformerRestController(IPerformer performerServiceo, IEventLogger logger)
        {
            _performerService = performerServiceo;
            _logger = logger;
        }

        [HttpGet("{id}")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> GetById(int id)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Get performer called id:" + id);

                var performer = await _performerService.GetPerformerById(id);
                if (performer == null)
                {
                    _logger.Log(LogLvl.Warning, "Performer not found with id:" + id);
                    return NotFound();
                }

                _logger.Log(LogLvl.Information, "Performer found for id:" + id);
                return Ok(performer);
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, $"Error retrieving performer id:{id}");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }

        [HttpPost]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> Create(CreatePerformerDTO dto)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Create performer called dto:" + dto.ToString());

                PerformerDTO performerDTO = new PerformerDTO();
                if (dto.Name != null)
                    performerDTO.Name = dto.Name;

                if (dto.LastName != null)
                    performerDTO.LastName = dto.LastName;

                int id = await _performerService.CreatePerformer(performerDTO);

                _logger.Log(LogLvl.Information, "Performer created with id: " + id);
                return Ok("Performer created with id: " + id);
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error creating performer");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }

        [HttpPatch]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> Update(EditPerformerDTO dto)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Update performer called dto:" + dto.ToString());

                PerformerDTO performerDTO = new PerformerDTO();
                performerDTO.Name = dto.Name;
                performerDTO.Id = dto.Id;

                if (dto.LastName != null)
                    performerDTO.LastName = dto.LastName;

                bool updated = await _performerService.UpdatePerformer(performerDTO);
                if (updated != true)
                {
                    return NotFound("Performer not found with id:" + dto.Id);
                }

                _logger.Log(LogLvl.Information, "Performer updated");
                return Ok("Performer updated: " + dto.ToString());
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error creating performer");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }

        }

        [HttpDelete("{id}")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> Delete(int id)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Delete performer called id:" + id);

                bool deleted = await _performerService.DeletePerformer(id);
                if (!deleted)
                {
                    _logger.Log(LogLvl.Warning, "Performer don't exist id:" + id);
                    return NotFound();
                }

                _logger.Log(LogLvl.Information, "Delete performer called dto:" + id);
                return Ok();
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error deleting performer");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }

        [HttpGet("search")]
        [AllowAnonymous]
        public async Task<IActionResult> Search(string? search, int page = 1, int pageSize = 10)
        {
            try
            {
                var filter = new PerformerFilter
                {
                    Search = search,
                    Page = page,
                    PageSize = pageSize
                };

                var result = await _performerService.GetPerformersByFilter(filter);
                if (result == null || result.TotalCount == 0)
                {
                    return NotFound();
                }

                return Ok(result.Performers);
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error searching performer");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }
    }
}
