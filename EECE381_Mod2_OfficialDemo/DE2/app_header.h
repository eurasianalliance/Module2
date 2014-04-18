/*
 * app_header.h
 *
 *  Created on: Mar 19, 2014
 *      Author: Team 13
 */

#ifndef APP_HEADER_H_
#define APP_HEADER_H_

#include <stdio.h>
#include "altera_up_avalon_video_pixel_buffer_dma.h"
#include "altera_up_avalon_video_character_buffer_with_dma.h"
#include "altera_up_avalon_audio_and_video_config.h"
#include "altera_up_avalon_audio.h"
#include "altera_up_sd_card_avalon_interface.h"

#include "sys/alt_timestamp.h"
#include "sys/alt_alarm.h"
#include "sys/alt_irq.h"
#include "altera_up_avalon_rs232.h"
#include <string.h>

#define MAX 10
#define ACTIONS_NUMBER 6
#define READY 73
#define TRUE 1
#define FALSE 0
#define CHAR_BUF_LEN 5
#define WEAKNESS_LENGTH 1 // depending on the # of players
#define LIFE_SIZE 3

struct player{

	int life[LIFE_SIZE];
	int action[ACTIONS_NUMBER];

};

// --- Villain's Weakness ---
struct weakness{
	int weakness[WEAKNESS_LENGTH];
	int life;
};

//void setRandomActions(struct player* pl);
void sendActions(struct player* pl, alt_up_rs232_dev* de2) ;
void sendLife(struct player* pl, alt_up_rs232_dev* de2) ;
void setRandomActions(struct player* pl, struct player* p2, struct player* p3,struct player* p4);
void chooseWeakness(struct player* pl, struct weakness* vln, int id);
void delay();
void long_delay();

#endif /* APP_HEADER_H_ */
