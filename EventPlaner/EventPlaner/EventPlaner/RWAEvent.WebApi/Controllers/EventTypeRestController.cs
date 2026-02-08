using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using RWAEvent.BL.Models.Filters;
using RWAEvent.BL.Services;
using RWAEvent.BL.Models.Enums;
using RWAEvent.BL.Models.DTO.EventType;

namespace RWAEvent.WebApi.Controllers
{
    [Route("api/eventType")]
    [ApiController]
    public class EventTypeRestController : ControllerBase
    {
        private readonly IEventLogger _logger;
        private readonly IEventType _eventTypeService;

        public EventTypeRestController(IEventType eventTypeService, IEventLogger logger)
        {
            _eventTypeService = eventTypeService;
            _logger = logger;
        }


        [HttpGet("{id}")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> GetById(int id)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Get event type called id:" + id);

                var env = await _eventTypeService.GetEventTypeById(id);
                if (env == null)
                {
                    _logger.Log(LogLvl.Warning, "Event type not found with id:" + id);
                    return NotFound();
                }

                _logger.Log(LogLvl.Information, "Event type found for id:" + id);
                return Ok(env);
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, $"Error retrieving event id:{id}");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }

        [HttpPost]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> Create(CreateEventTypeDTO dto)
        {
            try
            {
                EventTypeDTO eventDTO = new EventTypeDTO();
                eventDTO.Name = dto.Name;

                _logger.Log(LogLvl.Information, "Create event type called dto:" + dto.ToString());

                int id = await _eventTypeService.CreateEventType(eventDTO);

                _logger.Log(LogLvl.Information, "Event type created with id:" + id);
                return Ok(id);
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error creating event type");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }

        }

        [HttpPatch]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> Update(EventTypeDTO dto)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Update event type called dto:" + dto.ToString());

                bool updated = await _eventTypeService.UpdateEventType(dto);

                if (!updated)
                {
                    return NotFound("Event type with do not exist id: " + dto.Id);
                }

                _logger.Log(LogLvl.Information, "Event type updated");
                return Ok("Event type updated");
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error updating event");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }

        }

        [HttpDelete("{id}")]
        [Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
        public async Task<IActionResult> Delete(int id)
        {
            try
            {
                _logger.Log(LogLvl.Information, "Delete event type called id:" + id);

                bool deleted = await _eventTypeService.DeleteEventType(id);
                if (!deleted)
                {
                    _logger.Log(LogLvl.Warning, "Event type don't exist id:" + id);
                    return NotFound();

                }

                _logger.Log(LogLvl.Information, "Delete event type deleted:" + id);
                return Ok();
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error deleting event type");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }

        [HttpGet("/search")]
        [AllowAnonymous]
        public async Task<IActionResult> Search(string? search, int page = 1, int pageSize = 6)
        {
            try
            {
                var filter = new EventTypeFilter
                {
                    Search = search,
                    Page = page,
                    PageSize = pageSize
                };

                var result = await _eventTypeService.GetEventTypesByFilter(filter);

                if (result == null || result.TotalCount == 0)
                {
                    return NotFound();
                }

                return Ok(result.EventTypes);
            }
            catch (Exception e)
            {
                _logger.Log(LogLvl.Error, "Error searching event");
                return StatusCode(StatusCodes.Status500InternalServerError, e.Message);
            }
        }
    }
}
