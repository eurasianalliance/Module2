/*
 * bitmap_platform.h
 *
 *  Created on: 2014-04-05
 *      Author: Yoji
 */

#ifndef BITMAP_PLATFORM_H_
#define BITMAP_PLATFORM_H_

#include <stdio.h>
#include "altera_up_avalon_video_pixel_buffer_dma.h"
#include "altera_up_avalon_video_character_buffer_with_dma.h"
#include "altera_up_avalon_audio_and_video_config.h"
#include "altera_up_avalon_audio.h"
#include "altera_up_sd_card_avalon_interface.h"

#include "sys/alt_timestamp.h"
#include "sys/alt_alarm.h"
#include "sys/alt_irq.h"

void draw_title(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void draw_face(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void draw_hurtface(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void draw_deadface(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void draw_fire_sides(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void draw_light_sides(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void draw_doomdevice(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void full_health_bar(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void health_bar_min1(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void health_bar_min2(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void health_bar_min3(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void health_bar_min4(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void health_bar_min5(alt_up_pixel_buffer_dma_dev* pixel_buffer);

void phealth_bar_min5(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void phealth_bar_min4(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void phealth_bar_min3(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void phealth_bar_min2(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void phealth_bar_min1(alt_up_pixel_buffer_dma_dev* pixel_buffer);
void full_phealth_bar(alt_up_pixel_buffer_dma_dev* pixel_buffer);

#endif /* BITMAP_PLATFORM_H_ */
